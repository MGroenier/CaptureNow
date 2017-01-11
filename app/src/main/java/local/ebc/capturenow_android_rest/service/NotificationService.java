package local.ebc.capturenow_android_rest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import local.ebc.capturenow_android_rest.R;
import local.ebc.capturenow_android_rest.model.Capture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Martijn on 11/01/2017.
 */

public class NotificationService extends Service {

    public static final String TAG = "NotificationService";
    private Timer timer = new Timer();
    private CaptureService client;
    private Date timestampLatestCapture;

    @Override
    public void onCreate() {
        super.onCreate();
        client = ServiceGenerator.createService(CaptureService.class);
        timestampLatestCapture = new Date();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Log.i(TAG, "NotificationService polled for new content!");

                Call<Capture> call = client.getLatestCapture();
                call.enqueue(new Callback<Capture>() {
                                 @Override
                                 public void onResponse(Call<Capture> call, Response<Capture> response) {
                                     Capture latestCapture = response.body();
                                     Log.d(TAG, "latestCapture.getTimestamp(): " + latestCapture.getTimestamp() + " _____ " + timestampLatestCapture);
                                     if(latestCapture.getTimestamp().after(timestampLatestCapture)) {
                                         timestampLatestCapture = new Date();
                                         Log.d(TAG, "A more recent capture was found! " + latestCapture.getTitle() + " " + latestCapture.getTimestamp());

                                         Intent intent = new Intent();
                                         intent.setAction("local.ebc.capturenow_android_rest.NEW_CONTENT_NOTIFICATION");
                                         sendBroadcast(intent);

                                         NotificationCompat.Builder mBuilder =
                                                 new NotificationCompat.Builder(getBaseContext())
                                                         .setSmallIcon(R.drawable.ic_panorama_black_24dp)
                                                         .setContentTitle("CapNow")
                                                         .setContentText("Somebody uploaded a new capture!")
                                                         .setPriority(Notification.PRIORITY_MAX)
                                                         .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.image_icon));

                                         int mNotificationId = 001;
                                         NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                         mNotificationManager.notify(mNotificationId, mBuilder.build());

                                     }
                                     else {
                                         Log.d(TAG, "onResponse() NO NEW CONTENT! ");
                                     }
                                 }

                                 @Override
                                 public void onFailure(Call<Capture> call, Throwable t) {
                                     Log.d(TAG, "onFailure() FAILED!");
                                 }
                             });

            }
        }, 0, 5000);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
