package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Instant;

import java.util.Comparator;
import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.service.CarClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCars extends AppCompatActivity{

    public static final String TAG = Main_drawer.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private static LatLng user;

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_cars);
        Window w = this.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));
        Intent i = getIntent();
        String userPos = i.getStringExtra("Userlocation");
        String[] latlng = userPos.split(";");
        user = new LatLng(Double.parseDouble(latlng[0]),Double.parseDouble(latlng[1]));
        CarClient client = GenericService.getClient(CarClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
        Call<List<CarCurrent>> carCall = client.getCars();
        carCall.enqueue(new Callback<List<CarCurrent>>() {
            @Override
            public void onResponse(Call<List<CarCurrent>> call, Response<List<CarCurrent>> response) {
                final List<CarCurrent> cars = response.body();
                final ListView lv = (ListView) findViewById(R.id.carList);
                cars.sort(new Comparator<CarCurrent>() {
                    @Override
                    public int compare(CarCurrent o1, CarCurrent o2) {
                        return o2.getFuelLevel() - o1.getFuelLevel();
                    }
                });
                ArrayAdapter<CarCurrent> listAdapterLV = new ArrayAdapter<CarCurrent>(ShowCars.this, R.layout.simplerow, cars){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        // Get the current item from ListView
                        View view = super.getView(position,convertView,parent);
                        if(position %2 == 1)
                        {
                            // Set a background color for ListView regular row/item
                            view.setBackgroundColor(Color.parseColor("#FF00AAE0"));
                        }
                        else
                        {
                            // Set the background color for alternate row/item
                            view.setBackgroundColor(Color.parseColor("#FF009EE0"));
                        }
                        return view;
                    }
                };
                lv.setAdapter(listAdapterLV);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        Intent i = new Intent(ShowCars.this, AssignCar.class);
                        Object listItem = lv.getItemAtPosition(position);
                        CarCurrent carCur = (CarCurrent) listItem;
                        i.putExtra("carVin", carCur.getCar().getVin());
                        ShowCars.this.startActivity(i);
                    }
                });

                final Spinner sp = (Spinner) findViewById(R.id.dropDown);
                Drawable spinnerDrawable = sp.getBackground().getConstantState().newDrawable();
                spinnerDrawable.setColorFilter(getResources().getColor(R.color.car2goBlue), PorterDuff.Mode.SRC_ATOP);
                sp.setBackground(spinnerDrawable);

                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object listItem = sp.getItemAtPosition(position);
                        String sel = listItem.toString();
                        if ("Fuellevel".equals(sel)) {
                            cars.sort(new Comparator<CarCurrent>() {
                                @Override
                                public int compare(CarCurrent o1, CarCurrent o2) {
                                    return o2.getFuelLevel() - o1.getFuelLevel();
                                }
                            });
                            lv.setAdapter(updateList(cars));
                        } else if ("Distance".equals(sel)) {
                            cars.sort(new Comparator<CarCurrent>() {
                                @Override
                                public int compare(CarCurrent o1, CarCurrent o2) {
                                    if (distance(user.latitude, user.longitude, o1.getLat().doubleValue(), o1.getLng().doubleValue()) < distance(user.latitude, user.longitude, o2.getLat().doubleValue(), o2.getLng().doubleValue())) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                }
                            });
                            lv.setAdapter(updateList(cars));
                        }else if("Idletime".equals(sel)){
                            cars.sort(new Comparator<CarCurrent>() {
                                @Override
                                public int compare(CarCurrent o1, CarCurrent o2) {
                                    Long idleLong1 = Instant.now().getMillis() - o1.getCarCurrentPK().getTimestamp().getTime();
                                    Long idleLong2 = Instant.now().getMillis() - o2.getCarCurrentPK().getTimestamp().getTime();
                                    return idleLong1.intValue() - idleLong2.intValue();
                                }
                            });
                            lv.setAdapter(updateList(cars));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<CarCurrent>> call, Throwable t) {
                Log.e(TAG, "CarCall in AssignCar failed");
            }
        });
    }

    private double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Double(distance * meterConversion).floatValue();
    }

    private ArrayAdapter<CarCurrent> updateList(List<CarCurrent> list){
        ArrayAdapter<CarCurrent> listAdapterLV = new ArrayAdapter<CarCurrent>(ShowCars.this, R.layout.simplerow, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#FF00AAE0"));
                }
                else
                {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#FF009EE0"));
                }
                return view;
            }
        };
        return listAdapterLV;
    }

}
