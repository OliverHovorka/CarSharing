package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.service.DataBean;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GeofenceTransitionsIntentService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.UserClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Main_drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = Main_drawer.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static Bitmap smallMarkerPerson;
    private static GoogleMap gmap;
    private static Marker userMarker;
    private static Bitmap smallMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "FirebaseException");
                } else {
                    String token = task.getResult().getToken();
                    Log.e(TAG, "TOKEN: " + token);
                }
            }
        });



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.person_marker, null);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarkerPerson = Bitmap.createScaledBitmap(b, 200, 200, false);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) {
            Log.e(TAG, "GOOGLEMAP IS NULL");
            return;
        }
        gmap = googleMap;
        if(DataBean.getUserPosition() != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(DataBean.getUserPosition()));
        }
        gmap.setOnMarkerClickListener(this);
        gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        LatLng standort = new LatLng(48.21809, 16.2660599);
        BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.car_marker, null);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        if(DataBean.getCurJob() == null) {
            for (CarCurrent c : DataBean.getCarList()) {
                LatLng pos = new LatLng(c.getLat().doubleValue(), c.getLng().doubleValue());
                Marker m = gmap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(c.getCar().getVin())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                m.setTag(c);
            }
        }else{
            if(DataBean.getCurCar() != null) {
                LatLng pos = new LatLng(DataBean.getCurCar().getLat().doubleValue(), DataBean.getCurCar().getLng().doubleValue());
                Marker m = gmap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(DataBean.getCurCar().getCar().getVin())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                m.setTag(DataBean.getCurCar());
            }else{
                Log.e(TAG,"DataBean curCar is null");
            }
        }
        googleMap.setMinZoomPreference(12.5f);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Intent i;
        if (id == R.id.current_task) {
            if (DataBean.getCurJob() == null) {
                Toast.makeText(Main_drawer.this, "User has no Job currently", Toast.LENGTH_LONG).show();
            } else {
                i = new Intent(this, CurTask.class);
                Main_drawer.this.startActivity(i);
            }

        } else if (id == R.id.prev_tasks) {
            i = new Intent(this, PrevTasks.class);
            this.startActivity(i);
        } else if (id == R.id.show_cars) {
            i = new Intent(this, ShowCars.class);
            this.startActivity(i);
        } else if (id == R.id.logout) {
            DataBean.reset();
            i = new Intent(this,Login.class);
            this.startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if(DataBean.getUserPosition() != null && gmap != null){
            gmap.moveCamera(CameraUpdateFactory.newLatLng(DataBean.getUserPosition()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(DataBean.getUserPosition() != null){
            gmap.moveCamera(CameraUpdateFactory.newLatLng(DataBean.getUserPosition()));
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    @SuppressWarnings("")
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                handleNewLocation(location);
            }
        } else {

        }
    }

    private void handleNewLocation(Location location) {
        if (gmap == null) {
            Log.e(TAG, "GOOGLEMAP IS NULL HANDLENEWLOC");
            return;
        }
        if (userMarker != null) {
            userMarker.remove();
        }else{
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            DataBean.setUserPosition(latLng);
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        UserClient userPos = GenericService.getClient(UserClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
        BigDecimal lat = new BigDecimal(location.getLatitude());
        BigDecimal lng = new BigDecimal(location.getLongitude());
        if(DataBean.getUser() != null) {
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
            DataBean.setUserPosition(latLng);
            userMarker = gmap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("User")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerPerson)));
            Log.i(TAG, "New Marker Added MAIN DRAWER");
        }else{
            Log.e(TAG,"DataBean user is null");
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
    public void onLocationChanged(Location location) {
        Log.i(TAG, "LOCATION CHANGED MAIN");
        handleNewLocation(location);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(TAG, "MARKER CLICKED");
        if ("User".equals(marker.getTitle())) {
            Toast.makeText(this, "Can not navigate to user!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(DataBean.getCurJob() == null) {
                Intent i = new Intent(this, AssignCar.class);
                DataBean.setCurCar((CarCurrent) marker.getTag());
                this.startActivity(i);
                return true;
            }else{
                Toast.makeText(this,"User already has a job", Toast.LENGTH_LONG).show();
                return true;
            }
        }
    }

}

