package at.ac.htlstp.carsharing.app.carsharingapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import at.ac.htlstp.carsharing.app.carsharingapp.activities.CurTask;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.Login;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.Main_drawer;

public class FirebaseMessageService extends FirebaseMessagingService {

    public static final String TAG = FirebaseMessageService.class.getSimpleName();
    public static String currentToken= null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "FIRE:From: " + remoteMessage.getFrom());
        Log.e(TAG, "FIRE MSG: " + remoteMessage.toString());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "FIRE Message data payload: " + remoteMessage.getData());
            Intent i = new Intent(this, Login.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "carChannelId")
                    .setContentTitle("Firebase MsgId: " + remoteMessage.getMessageId())
                    .setContentText("Firebase MsgTime: " + remoteMessage.getSentTime())
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "FIRE Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        currentToken = s;
        Log.e(TAG,"FIRE TOKEN: " + s);
    }
}
