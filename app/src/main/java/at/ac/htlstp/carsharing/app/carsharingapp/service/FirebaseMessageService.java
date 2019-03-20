package at.ac.htlstp.carsharing.app.carsharingapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.CurTask;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.Login;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.Main_drawer;
import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;

public class FirebaseMessageService extends FirebaseMessagingService {

    public static final String TAG = FirebaseMessageService.class.getSimpleName();
    public static String currentToken= null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "FIRE:From: " + remoteMessage.getFrom());
        Log.i(TAG, "FIRE MSG: " + remoteMessage.toString());
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(GeofenceTransitionsIntentService.NOTIFICATION_SERVICE);
        String channelId = "carChannelId";
        CharSequence channelName = "carChannel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "FIRE Message data payload: " + remoteMessage.getData());
            Log.i(TAG,"FIRE Data:" + remoteMessage.getData());


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "FIRE Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        currentToken = s;
        Log.i(TAG,"FIRE TOKEN: " + s);
    }
}

