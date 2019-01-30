package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.service.CarClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.JobClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.MyLocationListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignCar extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMapLongClickListener {
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = Main_drawer.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static Bitmap smallMarkerPerson;
    private static GoogleMap gmap;
    private static Marker userMarker;
    private int userID;
    private String vin;
    private static Polyline poly;
    private static Polyline poly2;
    private static LocationManager mLocationManager;
    private static CarCurrent curCar;
    private static Marker carMarker;
    private static Marker destMarker;
    private static com.google.maps.model.LatLng dest;
    private static Bitmap carIcon;
    private static boolean fixed = false;

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.assign_car);
        Window w = this.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));
        Intent i = getIntent();
        vin = i.getStringExtra("carVin");
        userID = i.getIntExtra("userID", -1);
        if (userID == -1) {
            Log.e(TAG, "USERID couldnt be transferred");
            Toast.makeText(AssignCar.this, "UserID could not be transferred", Toast.LENGTH_LONG).show();
        }

        CarClient client = GenericService.getClient(CarClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
        Call<CarCurrent> carCall = client.getCar(vin);
        carCall.enqueue(new Callback<CarCurrent>() {
            @Override
            public void onResponse(Call<CarCurrent> call, Response<CarCurrent> response) {
                curCar = response.body();
                Long idletime = Instant.now().getMillis() - curCar.getCarCurrentPK().getTimestamp().getTime();
                Date idle = new Date(idletime);
                makeHeaderString(curCar.getCar().getModel(), curCar.getCar().getPlateNumber(), curCar.getCar().getFuelType().getType(), curCar.getFuelLevel() + "", idle.getHours() + "H " + idle.getMinutes() + "min");
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapViewCurTask);
                mapFragment.getMapAsync(AssignCar.this);
            }

            @Override
            public void onFailure(Call<CarCurrent> call, Throwable t) {
                Log.e(TAG, "CarCall in AssignCar failed");
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        /*-------------------- Location Listener ---------------------------*/
        // The minimum time (in miliseconds) the system will wait until checking if the location changed
        int minTime = 1000;
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

        BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.person_marker, null);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarkerPerson = Bitmap.createScaledBitmap(b, 200, 200, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        gmap.setOnMapLongClickListener(this);
        LatLng wien = new LatLng(48.241696, 16.372928);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        carIcon = smallMarker;
        LatLng pos = new LatLng(curCar.getLat().doubleValue(), curCar.getLng().doubleValue());

        carMarker = googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(curCar.getCar().getVin())
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        //Marker mk = googleMap.addMarker(new MarkerOptions().position(wien).title("Wien").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car_fullsale)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(wien));
        googleMap.setMinZoomPreference(7.0f);
        googleMap.setMaxZoomPreference(60.0f);
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    public void handleNewLocation(Location location) {
        Log.e(TAG, "Handle New Location CurTask");
        if (userMarker != null) {
            userMarker.remove();
        }
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        if (gmap != null) {
            userMarker = gmap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("User")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerPerson)));
            Log.e(TAG, "New Marker Added CURTASK");
            makeRoute();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "LOCATION CHANGED CURTASK");
        handleNewLocation(location);
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
        sb.append(idleTime);
        t.setText(sb.toString());
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }


    public void makeRoute() {
        DateTime now = new DateTime();
        try {
            com.google.maps.model.LatLng userloc = new com.google.maps.model.LatLng(userMarker.getPosition().latitude, userMarker.getPosition().longitude);
            com.google.maps.model.LatLng carPos = new com.google.maps.model.LatLng(carMarker.getPosition().latitude, carMarker.getPosition().longitude);
            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(userloc)
                    .destination(carPos).departureTime(now)
                    .await();
            addPolyline(result);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeRoutewithDest(){
        DateTime now = new DateTime();
        try {
            com.google.maps.model.LatLng userloc = new com.google.maps.model.LatLng(userMarker.getPosition().latitude, userMarker.getPosition().longitude);
            com.google.maps.model.LatLng carPos = new com.google.maps.model.LatLng(carMarker.getPosition().latitude, carMarker.getPosition().longitude);
            DirectionsResult resultCar = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(userloc)
                    .destination(carPos).departureTime(now)
                    .await();
            DirectionsResult resultDest = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(carPos)
                    .destination(dest).departureTime(now)
                    .await();
            addPolyline(resultCar);
            addSecondPolyline(resultDest);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addPolyline(DirectionsResult results) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        poly = gmap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private void addSecondPolyline(DirectionsResult result){
        if(poly2 != null){
            poly2.remove();
        }
        List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        poly2 = gmap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    public void assignJob(View v) {
        if(dest!=null) {
            Intent i = new Intent(this, CurTask.class);
            JobClient jclient = GenericService.getClient(JobClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            Call<Boolean> callJob = jclient.createJob(userID, vin);
            callJob.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Log.e(TAG, "Job creation: uid:" + userID + " vin: " + vin);
                    Log.i(TAG, "Job creation status: " + response.code());
                    Log.i(TAG, "Job creation: " + response.body());
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
            i.putExtra("carVin", vin);
            i.putExtra("userID", userID);
            i.putExtra("destLatitude", dest.lat);
            i.putExtra("destLongitude",dest.lng);
            this.startActivity(i);
        }else{
            Toast.makeText(this,"Keine Destination gestzt",Toast.LENGTH_LONG).show();
        }
    }

    public void setDestination(View v) {
        fixed = true;
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        if(!fixed) {
            dest = new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
            if (gmap != null) {
                if (destMarker != null) {
                    destMarker.remove();
                }
                destMarker = gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Dest")
                        .icon(BitmapDescriptorFactory.fromBitmap(carIcon)));
                makeRoutewithDest();
            }
        }
    }
}




