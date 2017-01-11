package local.ebc.capturenow_android_rest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import local.ebc.capturenow_android_rest.R;
import local.ebc.capturenow_android_rest.adapter.CaptureListItemAdapter;
import local.ebc.capturenow_android_rest.api.CapNowAPI;
import local.ebc.capturenow_android_rest.fragment.CameraFragment;
import local.ebc.capturenow_android_rest.model.Capture;
import local.ebc.capturenow_android_rest.service.NotificationService;
import local.ebc.capturenow_android_rest.api.APIClientGenerator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Emil Claussen on 15.12.2016.
 */

public class MainActivity extends AppCompatActivity implements Callback<List<Capture>>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double lat,lon;
    private boolean capturing;
    private CapNowAPI client;
    private static final String TAG = "MainActivity";
    private File file;

    Context context;
    FragmentManager manager;
    FragmentTransaction transaction;
    Fragment fragment;

    Capture capture;
    List<Capture> list;

    CaptureListItemAdapter adapter;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, NotificationService.class));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildGoogleApiClient();
        context = this;

        // Used by the camerafragment.
        capturing = false;
        manager = getSupportFragmentManager();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        // Show newest captures first.
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Set up the recyclerview.
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new CaptureListItemAdapter(list, context);
        recyclerView.setAdapter(adapter);

        // Set up the CapNowAPI and retrieve all captures from server.
        client = APIClientGenerator.createService(CapNowAPI.class);
        getCaptures();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!capturing){
                    // Load the camera fragment.
                    loadFragment();

                    // Connect the GPS API.
                    mGoogleApiClient.connect();
                } else {
                    // Remove the camera fragment.
                    manager = getSupportFragmentManager();
                    transaction = manager.beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                    capturing = false;

                    // Disconnect the GPS API.
                    mGoogleApiClient.disconnect();

                    recyclerView.scrollToPosition(list.size()-1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("local.ebc.capturenow_android_rest.NEW_CONTENT_NOTIFICATION");
        registerReceiver(newContentBroadcastReceiver, filter);
        super.onResume();
    }

    // Callback called when a picture is taken in the CameraFragment.
    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            createCapture(data);
            uploadCapture();
        }
    };

    // Create a capture and add it to the RecyclerView.
    private void createCapture(byte[] data){
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minutes = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR);
        String str = "Capture" + Integer.toString(hour) + Integer.toString(minutes) + Integer.toString(seconds);
        capture = new Capture("0", str, lat, lon, data);

        bytesToFile(data);

        list.add(capture);
        adapter.notifyDataSetChanged();
    }

    // Load the CameraFragment.
    private void loadFragment(){
        capturing = true;
        fragment = new CameraFragment();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
    }

    // Initialize the client for Google API communication.
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_getcaptures) {
            getCaptures();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Function called when GPS location changes.
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        Toast toast = Toast.makeText(this, "Retrieving location..", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }


    //Function called when the Google API client is connected.
    @Override
    public void onConnected(Bundle bundle) {
        //Define the request.
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Get coordinates if a location is known.
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    //Save the jpeg-encoded byte array to a temporary file.
    public void bytesToFile(byte[] data){
        try {
            file = File.createTempFile("capture", null, this.getCacheDir());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void uploadCapture() {
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("capture-file", file.getName(), requestFile);

        // add another part within the multipart request
        RequestBody titlePart =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), capture.getTitle());

        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), capture.getLatitude().toString());

        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), capture.getLongitude().toString());

        RequestBody timestamp = RequestBody.create(MediaType.parse("multipart/form-data"), new Date().toString());

        Call<ResponseBody> call = client.createCapture(body, titlePart, latitude, longitude, timestamp);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse() SUCCESS!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure: FAILED!", t.getMessage());
            }
        });
    }

    //Retrieve all captures from server.
    private void getCaptures() {
        Call<List<Capture>> call = client.listCaptures();
        call.enqueue(this);
    }

    //If response contains captures, display them in the RecyclerView.
    @Override
    public void onResponse(Call<List<Capture>> call, Response<List<Capture>> response) {
        if(response.isSuccessful()) {
            Log.d(TAG, "Request successful");
            list.clear();
            for (Capture capture : response.body()){
                list.add(capture);
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(list.size());
        }
    }

    @Override
    public void onFailure(Call<List<Capture>> call, Throwable t) {
        Log.e(TAG, "Request NOT successful", t);
    }

    private BroadcastReceiver newContentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "New content on server!", Toast.LENGTH_LONG).show();
            Log.d("BROADCAST", "RECEIVED A BROADCAST!!!");

//            Intent restartIntent = getIntent();
//            finish();
//            startActivity(restartIntent);

            getCaptures();

        }
    };

}
