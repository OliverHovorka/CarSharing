package at.ac.htlstp.carsharing.app.carsharingapp.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.activities.Main_drawer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GeofenceTransitionsIntentService extends IntentService {

    public static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService() {
        super("carsharing_geofence");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = geofencingEvent.getErrorCode() + "";
            Log.e(TAG, "Geofence error : " + errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            StringBuilder sb = new StringBuilder();
            for(Geofence g : triggeringGeofences){
                    sb.append(g.getRequestId());
            }
            Intent i = new Intent(this, Main_drawer.class);
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
                    .setContentTitle("Geofence Entered: " + sb.toString())
                    .setContentText("Geofence Entered: " + sb.toString())
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            JobClient client = GenericService.getClient(JobClient.class,"ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            Call<Boolean> jCall = client.finishJob(DataBean.getCurJob().getId());
            jCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.body()){
                        Log.i(TAG,"Job finished succ");
                        Toast.makeText(GeofenceTransitionsIntentService.this,"Job finished",Toast.LENGTH_LONG).show();
                    }else{
                        Log.e(TAG, "Job finished failed");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e(TAG, "Job finished Call failed");
                }
            });
            Toast.makeText(this,"Geofence entered: " + sb.toString(),Toast.LENGTH_LONG).show();
            Log.i(TAG, "Geofence entered: " + geofenceTransition);
        } else {
            // Log the error.
            Log.e(TAG, "GeofenceError later");
        }
    }
}
