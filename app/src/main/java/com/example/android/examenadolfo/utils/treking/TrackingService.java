package com.example.android.examenadolfo.utils.treking;

import static com.example.android.examenadolfo.utils.CONSTANTES.COLLECTION_GPS;
import static com.example.android.examenadolfo.utils.CONSTANTES.DATE;
import static com.example.android.examenadolfo.utils.CONSTANTES.LAT;
import static com.example.android.examenadolfo.utils.CONSTANTES.LATITUDE;
import static com.example.android.examenadolfo.utils.CONSTANTES.LON;
import static com.example.android.examenadolfo.utils.CONSTANTES.LONGITUDE;
import static com.example.android.examenadolfo.utils.treking.Constants.CHANNEL_ID;
import static com.example.android.examenadolfo.utils.treking.Constants.TITLE_PUSH;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.android.examenadolfo.R;
import com.example.android.examenadolfo.presentation.ui.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TrackingService extends IntentService {

    private static final String TAG = TrackingService.class.getSimpleName();
    private static final String ACTION_LOCATION_UPDATED = "location_updated";
    private static final String ACTION_REQUEST_LOCATION = "request_location";
   // private static final long TIME_INTERVAL = 6000; //60 sec
    private static final long TIME_INTERVAL = 30000; //5min
    private static final long MIN_TIME_INTERVAL = 1; //10 sec
    private static final long MIN_DISTANCE = 100; //m
    private static Location lastLocation;
    private static PendingIntent sPendingIntent;
    private static GoogleApiClient sGoogleApiClient;
    public TrackingService() {
        super(TAG);
    }


    public static void startLocationTracking(Context context) {
        if (sPendingIntent == null && sGoogleApiClient == null) {
            Intent intent = new Intent(context, TrackingService.class);
            intent.setAction(TrackingService.ACTION_REQUEST_LOCATION);
            context.startService(intent);
        }
    }

    public static void stopLocationTracking() {
        if (sPendingIntent != null && sGoogleApiClient != null) {
            FusedLocationApi.removeLocationUpdates(sGoogleApiClient, sPendingIntent);
            sGoogleApiClient = null;
            sPendingIntent = null;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        createNotificationChannel();
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_REQUEST_LOCATION.equals(action)) {
            Log.d("Success", "requestLocationInternal");
            requestLocationInternal();
        } else if (ACTION_LOCATION_UPDATED.equals(action)) {
            Log.d("Success", "locationUpdated");
            locationUpdated(intent);
        }
    }

    private void requestLocationInternal() {
        Log.v(TAG, ACTION_REQUEST_LOCATION);
        if (!PermissionUtils.checkFineLocationPermission(this)) {
            return;
        }
        sGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        ConnectionResult connectionResult = sGoogleApiClient.blockingConnect(
                Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && sGoogleApiClient.isConnected()) {

            Intent locationUpdatedIntent = new Intent(this, TrackingService.class);
            locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);
            Location location = FusedLocationApi.getLastLocation(sGoogleApiClient);
            if (location != null && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                lastLocation = location;
                Intent lastLocationIntent = new Intent(locationUpdatedIntent);
                lastLocationIntent.putExtra(
                        FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
                startService(lastLocationIntent);
            }
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(100)
                    .setSmallestDisplacement(MIN_DISTANCE)
                    .setFastestInterval(MIN_TIME_INTERVAL)
                    .setInterval(TIME_INTERVAL);
            sPendingIntent = PendingIntent.getService(this, 0, locationUpdatedIntent, 0);
            FusedLocationApi.requestLocationUpdates(
                    sGoogleApiClient, locationRequest, sPendingIntent);
        } else {

            Log.e(TAG, String.format(Constants.GOOGLE_API_CLIENT_ERROR_MSG,
                    connectionResult.getErrorCode()));
        }
    }

    private void locationUpdated(Intent intent) {
        Log.v(TAG, ACTION_LOCATION_UPDATED);
        final Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if (location != null && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            lastLocation = location;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put(LON, String.valueOf(lastLocation.getLongitude()));
            user.put(LAT, String.valueOf(lastLocation.getLatitude()));
            user.put(DATE, DateUtils.simpleDate());
            db.collection(COLLECTION_GPS)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            showNotification(String.valueOf(lastLocation.getLongitude()),String.valueOf(lastLocation.getLatitude()),DateUtils.simpleDate());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {  }
                    });

            }
        }

        private void showNotification(String lat,String lon,String date){
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(TITLE_PUSH)
                    .setContentText(LATITUDE + lat + LONGITUDE + lon + "  /  "+ date)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify( 0x998, builder.build());

        }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="namex";
            String description = "decx";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static Location getLastLocation() {
        return lastLocation;
    }



}
