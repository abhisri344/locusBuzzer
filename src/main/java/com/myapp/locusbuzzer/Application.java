package com.myapp.locusbuzzer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class Application extends android.app.Application {

    public static final String CHANNEL_ID = "Reminder";
    public static Stack<Integer> notificationIds = new Stack<>();

    @Override
    public void onCreate() {
        super.onCreate();
        createChannels();
    }

    private void createChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID,"Reminder Channel", NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("This Channel gives reminder to user");
            channel2.setSound(alarmSound, null);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);

        }
    }
}
