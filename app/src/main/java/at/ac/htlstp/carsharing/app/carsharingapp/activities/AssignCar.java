package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.android.gms.maps.CameraUpdate;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;
import at.ac.htlstp.carsharing.app.carsharingapp.service.DataBean;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.JobClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.MyLocationListener;
import at.ac.htlstp.carsharing.app.carsharingapp.service.UserClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
Diese Activity wird beim Click auf einen Auto Marker aufgerufen
Hier kann sich der User selbst ein Auto zuweisen entweder über eine Liste von Hotspots oder manuell durch langes klicken auf die Karte
Nach der Zuweisung wird automatisch die CurTask aufgerufen in welcher der Aktuelle Job angezeigt wird
 */

public class AssignCar extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMapLongClickListener {
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = AssignCar.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static Bitmap smallMarkerPerson;
    private static GoogleMap gmap;
    private static Marker userMarker;
    private static Polyline poly;
    private static Polyline poly2;
    private static LocationManager mLocationManager;
    private static Marker carMarker;
    private static Marker destMarker;
    private static com.google.maps.model.LatLng dest;
    private static Bitmap carIcon;
    private static Bitmap destIcon;
    private static boolean updated = false;
    private static boolean fixed = false;


    /**
    In dieser Methode werden diverse Funktionen Initialisiert
     GoogleApiClient wird initialisiert
     Eine Instanz vom LocationRequest wird angelegt
     */
    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.assign_car);
        Window w = this.getWindow();
        updated = false;
        dest = null;
        fixed = false;
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));
        if(DataBean.getCurCar() != null) {
            makeHeaderString(DataBean.getCurCar().getCar().getModel(), DataBean.getCurCar().getCar().getPlateNumber(),
                    DataBean.getCurCar().getCar().getFuelType().getType(),
                    DataBean.getCurCar().getFuelLevel() + "",
                    DataBean.getCurCar().getHourDifference() + "H " + DataBean.getCurCar().getMinuteDifference() + "min");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewCurTask);
        mapFragment.getMapAsync(AssignCar.this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.dest_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        destIcon = smallMarker;
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

        bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.person_marker, null);
        b = bitmapdraw.getBitmap();
        smallMarkerPerson = Bitmap.createScaledBitmap(b, 200, 200, false);
        }else{
            Log.e(TAG,"onCreate DataBean CurCar is null");
        }
    }


    /**
     * Diese Funktion wird aufgerufen sobald die Googlemap asynchron geladen wurde
     * Hier wird die Globale Variable gmap gesetzt
     * Es wird hier auf die Map das ausgewählte Auto als Marker gezeichnet, weiters wird die Kamera auf den User fokusiert
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(DataBean.getCurCar() != null) {
            gmap = googleMap;
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            gmap.setOnMapLongClickListener(this);
            LatLng wien = new LatLng(48.241696, 16.372928);
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
            carIcon = smallMarker;
            LatLng pos = new LatLng(DataBean.getCurCar().getLat().doubleValue(), DataBean.getCurCar().getLng().doubleValue());

            carMarker = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(DataBean.getCurCar().getCar().getVin())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            googleMap.setMinZoomPreference(15.0f);
        }else{
            Log.e(TAG,"onMapReady DataBean curCar is null");
        }
    }

    /**
     * In dieser Funktion wird der GoogleApiClient erneut verbunden
     */
    @Override
    protected void onResume() {
        updated = false;
        dest = null;
        fixed = false;
        super.onResume();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }else{
            Log.e(TAG,"Assign: onResume: " + mGoogleApiClient);
        }
    }

    /**
     * In dieser Funktion werden die Locationservices getrennt ebenso der GoogleApiClient
     */
    @Override
    protected void onPause() {
        updated = false;
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * In dieser Funktion werden die Locationservices erneut registriert
     * @param bundle
     */
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


    /**
     * Diese Funktion wird jedesmal aufgerufen wenn der LocationListener eine neue Position meldet
     * Es wird hier der Usermarker geupdated und weiters wird die Position an den Server gesendet
     * @param location
     */
    public void handleNewLocation(Location location) {
        if(DataBean.getUser() != null) {
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
                    Log.i(TAG, "Userposition update successful");
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e(TAG, "Userposition update failed");
                }
            });
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (gmap != null) {
                if(!updated){
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    updated = true;
                }
                DataBean.setUserPosition(latLng);
                userMarker = gmap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("User")
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerPerson)));
                makeRoute();
            }
        }else{
            Log.e(TAG,"handleNewLoc DataBean user is null");
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
        handleNewLocation(location);
    }

    /**
     * Hier wird der Text der im Head der Activity steht zusammengesetzt
     * @param car ist der Typ des anzuzeigenden Autos
     * @param plateNumber das Kennzeichen des anzuzeigenden Autos
     * @param fuelType der Typ an Fuel
     * @param fuelLevel der Tankfüllstand des anzuzeigenden Autos
     * @param idleTime die Idletime des anzuzeigenden Autos
     */
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

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }


    /**
     * Diese Methode zeichnet die Route zwischen dem User dem um zu parkenden Autos
     */
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

    /**
     * Diese Methode zeichnet die Route zwischen dem User dem um zu parkenden Autos und der Zieldestination
     */
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


    /**
     * Diese Funktion zeichnet die Routen auf die Karte
     * @param results
     */
    private void addPolyline(DirectionsResult results) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        poly = gmap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.rgb(0,158,224)));
    }

    /**
     * Diese Funktion zeichnet die Routen auf die Karte
     * @param result
     */
    private void addSecondPolyline(DirectionsResult result){
        if(poly2 != null){
            poly2.remove();
        }
        List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        poly2 = gmap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.rgb(0,158,224)));
    }

    /**
     * Diese funktion wird beim Click des Buttons "Assign Car"
     * Hier wird die Destination an den Server gesendet und ein Job für den eingeloggten User kreiert
     * @param v
     */
    public void assignJob(View v) {
        if(DataBean.getUser() != null) {
            if (dest != null) {
                final Intent i = new Intent(this, CurTask.class);
                JobClient jclient = GenericService.getClient(JobClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
                Call<Job> callJob = jclient.createJob(DataBean.getUser().getId(), DataBean.getCurCar().getCar().getVin(), dest.lat, dest.lng);
                callJob.enqueue(new Callback<Job>() {
                    @Override
                    public void onResponse(Call<Job> call, Response<Job> response) {
                        Log.i(TAG, "Job creation status: " + response.code());
                        if (response.body() != null) {
                            DataBean.setCurJob(response.body());
                            AssignCar.this.startActivity(i);
                            AssignCar.this.finish();
                            Log.i(TAG, "Job creation: uid:" + DataBean.getUser().getId() + " vin: " + DataBean.getCurCar().getCar().getVin());
                            Log.i(TAG, "Job creation status: " + response.code());
                            Log.i(TAG, "Job creation: " + response.body());
                        } else {
                            Log.e(TAG, "Job creation rejected : " + response.code());
                            Toast.makeText(AssignCar.this,"User already has a job",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Job> call, Throwable t) {
                        Log.e(TAG, "Job creation failed");
                    }
                });

            } else {
                Toast.makeText(this, "No destination set", Toast.LENGTH_LONG).show();
            }
        }else{
            Log.e(TAG,"assignJob DataBean user is null");
        }
    }

    /**
     * Diese Funktion überprüft ob die Destination schon fix gesetzt ist
     * @param v
     */
    public void setDestination(View v) {
        if(dest == null){
            Toast.makeText(this,"No destination set",Toast.LENGTH_LONG).show();
        }else if(fixed){
            Toast.makeText(this,"Destination already set",Toast.LENGTH_LONG).show();
        } else {
            fixed = true;
            Toast.makeText(this, "Destination set", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Diese Methode wird aufgerufen bei einem Langen Click auf die Googlemap
     * hierbei wird der Marker auch direkt auf die Karte gezeichnet
     * @param latLng
     */
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
                        .icon(BitmapDescriptorFactory.fromBitmap(destIcon)));
                makeRoutewithDest();
            }
        }else{
            Toast.makeText(this,"Destination already set",Toast.LENGTH_LONG).show();
        }
    }

}




