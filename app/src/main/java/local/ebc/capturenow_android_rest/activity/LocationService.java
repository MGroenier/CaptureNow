package local.ebc.capturenow_android_rest.activity;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import local.ebc.capturenow_android_rest.R;

/**
 * Created by ebc on 08.12.2016.
 */

public class LocationService extends IntentService {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Context context;

    public LocationService() {
        super("LocationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        getLocationData();
        context = this;
    }

    public void getLocationData() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // New locationlistener.
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //if (location.getAccuracy() > 20) {
                    // The location has sufficient accuracy, save it to global variable.

                    Toast toast = Toast.makeText(context, location.toString(), Toast.LENGTH_LONG);
                    toast.show();
                    closeUpdates();
                //}
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ManifestPermissions", "User denied permission");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void closeUpdates() {
        // Check for permissions and remove updates.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }

    }

}
