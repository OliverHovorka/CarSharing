package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.service.DataBean;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GeofenceTransitionsIntentService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.MyLocationListener;
import at.ac.htlstp.carsharing.app.carsharingapp.service.UserClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurTask extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = CurTask.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static Bitmap smallMarkerPerson;
    private static GoogleMap gmap;
    private static Marker userMarker;
    private static Marker carMarker;
    private static Marker destMarker;
    private static Polyline[] polys = new Polyline[0];
    private static LocationManager mLocationManager;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cur_task);
        Window w = this.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        geofencingClient = LocationServices.getGeofencingClient(this);
        Geofence geofence = new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(DataBean.getCurJob().getId() + "")
                .setCircularRegion(
                        DataBean.getCurJob().getDestLat(),
                        DataBean.getCurJob().getDestLng(),
                        DataBean.getGeoFenceRadius()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
        DataBean.setGeofence(geofence);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG,"Geofences registered successfully");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Geofences not registered");
                            e.printStackTrace();
                            Log.e(TAG,"Geofence: " + e.getMessage() + " Code: " + e.getCause() + " Class: " + e.getClass());
                        }
                    });
        }else{
            String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET};
            requestPermissions(perm,1);
        }

        if (DataBean.getCurCar() != null) {
            makeHeaderString(DataBean.getCurCar().getCar().getModel(), DataBean.getCurCar().getCar().getPlateNumber(),
                    DataBean.getCurCar().getCar().getFuelType().getType(), DataBean.getCurCar().getFuelLevel() + "",
                    DataBean.getCurCar().getHourDifference() + "H " + DataBean.getCurCar().getMinuteDifference() + "min");

            BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.person_marker, null);
            Bitmap b = bitmapdraw.getBitmap();
            smallMarkerPerson = Bitmap.createScaledBitmap(b, 200, 200, false);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapViewCurTask);
            mapFragment.getMapAsync(CurTask.this);

            /*-------------------- Location Listener ---------------------------*/
            // The minimum time (in miliseconds) the system will wait until checking if the location changed
            int minTime = 1000;
            // The minimum distance (in meters) traveled until you will be notified
            // The minimum distance (in meters) traveled until you will be notified
            float minDistance = 5;
            // Create a new instance of the location listener
            MyLocationListener myLocListener = new MyLocationListener(this);
            // Get the location manager from the system
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Get the criteria you would like to use
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            String bestProvider = LocationManager.GPS_PROVIDER;
            // Request location updates
            mLocationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);

            /*------------------------- End of Location Listener -------------------------------*/

        } else {
            Log.e(TAG, "onCreate DataBean curCar is null");
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, 0);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(DataBean.getGeofence());
        return builder.build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        LatLng wien = new LatLng(48.241696, 16.372928);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(wien));
        googleMap.setMinZoomPreference(10.0f);
        googleMap.setMaxZoomPreference(50.0f);
        setUpMarkers();
    }

    public void setUpMarkers() {
        if (DataBean.getCurCar() != null) {
            if (gmap != null) {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_marker);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
                LatLng pos = new LatLng(DataBean.getCurCar().getLat().doubleValue(), DataBean.getCurCar().getLng().doubleValue());
                LatLng dest = new LatLng(DataBean.getCurJob().getDestLat(), DataBean.getCurJob().getDestLng());
                carMarker = gmap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(DataBean.getCurCar().getCar().getVin())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.dest_marker);
                b = bitmapdraw.getBitmap();
                smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
                destMarker = gmap.addMarker(new MarkerOptions()
                        .position(dest)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                if (carMarker != null && destMarker != null && userMarker != null) {
                    makeRoute();
                }

            } else {
                Log.e(TAG, "SetUpMarkers DataBean curCar is null");
            }
        }else{
            Log.e(TAG,"Gmap setUpMarkers is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    @SuppressWarnings("")
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                handleNewLocation(location);
            }
        } else {

        }
    }

    public void handleNewLocation(Location location) {
        if (DataBean.getUser() != null) {
            if (userMarker != null) {
                userMarker.remove();
            }
            UserClient userPos = GenericService.getClient(UserClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            BigDecimal lat = new BigDecimal(location.getLatitude());
            BigDecimal lng = new BigDecimal(location.getLongitude());
            Call<Boolean> updateSucc = userPos.updateUserPosition(DataBean.getUser().getId(), lat, lng);
            updateSucc.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.body()) {
                        Log.i(TAG, "Userposition update successful");
                    } else {
                        Log.e(TAG, "Userposition update not accepted");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e(TAG, "Userposition update failed");
                }
            });
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            if (gmap != null) {
                DataBean.setUserPosition(latLng);
                userMarker = gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("User")
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerPerson)));
                setUpMarkers();
                Log.i(TAG, "New Marker Added CurTask");
            } else {
                Log.e(TAG, "Googlemap not loaded yet");
            }
        } else {
            Log.e(TAG, "handleNewLoc DataBean User is null");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }


    public void makeRoute() {
        if (DataBean.getCurCar() != null) {
            DateTime now = new DateTime();
            try {
                com.google.maps.model.LatLng userloc = new com.google.maps.model.LatLng(userMarker.getPosition().latitude, userMarker.getPosition().longitude);
                com.google.maps.model.LatLng carPos = new com.google.maps.model.LatLng(DataBean.getCurCar().getLat().doubleValue(), DataBean.getCurCar().getLng().doubleValue());
                DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING).origin(userloc)
                        .destination(carPos).departureTime(now)
                        .await();

                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(DataBean.getCurJob().getDestLat(), DataBean.getCurJob().getDestLng());
                DirectionsResult result2 = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING).origin(carPos)
                        .destination(destination).departureTime(now)
                        .await();

                addPolyline(result, result2);
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "makeRoute DataBean curCar is null");
        }
    }

    private void addPolyline(DirectionsResult... results) {
        Log.e(TAG,"Polyline: " + results.length);
        if(results.length < 2){
            Log.e(TAG,"Polyline dreck");
            return;
        }
        for (Polyline p : polys) {
            p.remove();
        }

        polys = new Polyline[results.length];
        for (int i = 0; i < results.length; i++) {
            List<LatLng> decodedPath = PolyUtil.decode(results[i].routes[0].overviewPolyline.getEncodedPath());
            polys[i] = gmap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.rgb(0, 158, 224)));
        }


    }

    public void makeHeaderString(String car, String plateNumber, String fuelType, String fuelLevel, String idleTime) {
        TextView t = (TextView) findViewById(R.id.car_details);
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Car: ");
        sb.append(car);
        sb.append("\n");
        sb.append("Plate number: ");
        sb.append(plateNumber);
        sb.append("\n");
        sb.append("Fuel type: ");
        sb.append(fuelType);
        sb.append("\n");
        sb.append("Fuel level: ");
        sb.append(fuelLevel);
        sb.append("\n");
        sb.append("Idletime: ");
        sb.append(idleTime);
        t.setText(sb.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "LOCATION CHANGED CURTASK");
        handleNewLocation(location);
    }

    public void redirectMaps(View v) {
        if (DataBean.getCurJob() != null) {
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + userMarker.getPosition().latitude + "," + userMarker.getPosition().longitude +
                    "&destination=" + DataBean.getCurJob().getDestLat() + "," + DataBean.getCurJob().getDestLng() +
                    "&waypoints=" + carMarker.getPosition().latitude + "," + carMarker.getPosition().longitude + "&travelmode=driving");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        } else {
            Log.e(TAG, "redirectMaps DataBean job is null");
        }
    }

    public void mainMenu(View v) {
        Intent i = new Intent(this, Main_drawer.class);
        this.startActivity(i);
        finish();
    }
}
