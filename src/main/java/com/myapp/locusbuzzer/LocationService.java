package com.myapp.locusbuzzer;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class LocationService extends Service {

    public double latitude = 0;
    public double longitude = 0;

    ArrayList<Task_Model> tasks = new ArrayList<>();
    Database database = new Database(this);
    public static final String SERVICE_MESSAGE = "ServiceMessage";
    private NotificationManagerCompat notificationManager;



    private ResultReceiver mResultReceiver;



    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {

            super.onLocationResult(locationResult);
            notificationManager= NotificationManagerCompat.from(getApplicationContext());
            if (locationResult != null && locationResult.getLastLocation() != null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();

//                Toast.makeText(LocationService.this, latitude+ "--------- "+longitude, Toast.LENGTH_SHORT).show();
                String data = latitude+" "+longitude;
//                Log.e("Location -", String.valueOf(locationResult));
//                Bundle bundle = new Bundle();
//                bundle.putString("Data", latitude+""+longitude);
//                bundle.putDoubleArray("Data", new double[]{latitude, longitude});
//                mResultReceiver.send(MyLocation.RESULT_OK, bundle);
                sendLocationtoUI(data);


                Cursor cursor = new Database(LocationService.this).getTasks();

                while(cursor.moveToNext()){
                    Task_Model task = new Task_Model(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
//

                    if(!task.getIsActive().equalsIgnoreCase("No")){
//                        Toast.makeText(LocationService.this, task.toString(), Toast.LENGTH_SHORT).show();

                        float distance[] = new float[10];
                        Location.distanceBetween(latitude, longitude,Double.parseDouble(task.getLatitude()),Double.parseDouble(task.getLongitude()),distance);
                        float range = (task.getRange());

                        if(distance[0] < range){

//                            NotificationCompat.Action markDone = markdone(task.getId());
                            Intent alarmIntent = new Intent(LocationService.this, MainActivity.class);
                            Application.notificationIds.push(task.getId());
//                            database.disable(String.valueOf(task.getId()));
                            alarmIntent.putExtra("Noti_ID", task.getId());
                            alarmIntent.putExtra( "NotificationMessage" , "I am from Notification" );
//                            alarmIntent.setAction(Intent. ACTION_MAIN ) ;
                            alarmIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
                            PendingIntent contentIntent = PendingIntent.getActivity(LocationService.this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), Application.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_alarm)
                                    .setContentTitle(task.getTitle()+" in range")
                                    .setContentText(task.getDesc())

                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                    .setContentIntent(contentIntent)
                                    .setSound(alarmSound)
//                                    .addAction(markDone)
                                    .addAction(R.drawable.ic_baseline_done_24, "Done", contentIntent)
//                                    .setAutoCancel(true)

//                                    .setOngoing(true)
                                    .build();

                            //                                    +"\n\nMark as Done"

                            notification.flags = Notification.FLAG_INSISTENT;
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                notification.visibility = Notification.VISIBILITY_PUBLIC;
//                            }
                            notificationManager.notify(task.getId(), notification);
//                            notificationManager.notify(111, notification);
//                            Toast.makeText(LocationService.this, task.getId()+" distance - "+distance[0], Toast.LENGTH_SHORT).show();
//                            task.setIsActive("No");
//                            database.disable(String.valueOf(task.getId()));
                            break;
                        }
//                        notificationManager.from(getApplicationContext()).cancel(task.getId());
                    }
//                    tasks.add(task);
                }

                Log.d("Location-Update", latitude + ", " + longitude);
            }
        }
    };

//    public NotificationCompat.Action markdone(int id){
//        Cursor cursor = new Database(LocationService.this).getTasks();
//        Application.notificationIds.add(id);
//        while(cursor.moveToNext()) {
//            Task_Model task = new Task_Model(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
//            if (task.getId() == id){
////                task.setIsActive("No");
//                database.disable(String.valueOf(id));
//            }
//        }
//
//        Intent doneIntent = new Intent(LocationService.this, MainActivity.class);
////        doneIntent.putExtra("Notification_ID", ""+id);
//        PendingIntent dismissIntent = PendingIntent.getActivity(LocationService.this, 0, doneIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Action doneAction = new NotificationCompat.Action.Builder(R.drawable.ic_done, "Click to End", dismissIntent)
//                .build();
////        notificationManager.cancel(id);
//
//
//        return doneAction;
//    }

    private void sendLocationtoUI(String data) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra("Data", data);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent );
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return null;
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";




        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

//        Toast.makeText(this, tasks.get(0).getTitle(), Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_location_icon2);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.PRIORITY_LOW);
        builder.setContentText("Locus Buzzer is accessing your location");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);

        builder.setPriority(NotificationCompat.PRIORITY_LOW);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_NONE
                );

                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow Location Permission by going into Settings", Toast.LENGTH_LONG).show();
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


//        Toast.makeText(this, "______________________________", Toast.LENGTH_SHORT).show();
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());


    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);

        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
//            System.out.println("------------------");
//            Toast.makeText(this, "----------------------", Toast.LENGTH_SHORT).show();
            if (action != null){
                if(action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
//                    Toast.makeText(this, "-----------start-----------", Toast.LENGTH_SHORT).show();
                    setResultReceiver((ResultReceiver)
                            intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER));
                    startLocationService();
                }else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)){
//                    Toast.makeText(this, "----------stop----------", Toast.LENGTH_SHORT).show();
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void setResultReceiver(ResultReceiver mResultReceiver) {
        this.mResultReceiver = mResultReceiver;
    }


}
