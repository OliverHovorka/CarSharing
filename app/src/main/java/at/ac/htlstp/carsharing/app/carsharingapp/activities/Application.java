package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import at.ac.htlstp.carsharing.app.carsharingapp.service.GeofenceTransitionsIntentService;

public class Application extends android.app.Application {

    public static final String TAG = android.app.Application.class.getSimpleName();
    public static String currentToken= null;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "FIRE FirebaseException");
                } else {
                    String token = task.getResult().getToken();
                    currentToken = token;
                    Log.e(TAG, "FIRE TOKEN: " + token);
                }
            }
        });
    }

    {
    }
}
