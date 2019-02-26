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
import android.widget.Toast;

import org.joda.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.service.DataBean;

public class ShowCars extends AppCompatActivity{

    public static final String TAG = ShowCars.class.getSimpleName();
    private static List<CarCurrent> carList =  new ArrayList<>();

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_cars);
        Window w = this.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));
        if(DataBean.getCarList() != null) {
            carList = DataBean.getCarList();
            carList.sort(new Comparator<CarCurrent>() {
                @Override
                public int compare(CarCurrent o1, CarCurrent o2) {
                    return o2.getFuelLevel() - o1.getFuelLevel();
                }
            });
            ArrayAdapter<CarCurrent> listAdapterLV = new ArrayAdapter<CarCurrent>(ShowCars.this, R.layout.simplerow, carList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the current item from ListView
                    View view = super.getView(position, convertView, parent);
                    if (position % 2 == 1) {
                        // Set a background color for ListView regular row/item
                        view.setBackgroundColor(Color.parseColor("#FF00AAE0"));
                    } else {
                        // Set the background color for alternate row/item
                        view.setBackgroundColor(Color.parseColor("#FF009EE0"));
                    }
                    return view;
                }
            };
            final ListView lv = (ListView) findViewById(R.id.carList);
            lv.setAdapter(listAdapterLV);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(ShowCars.this, AssignCar.class);
                    Object listItem = lv.getItemAtPosition(position);
                    CarCurrent carCur = (CarCurrent) listItem;
                    if(DataBean.getCurJob() == null) {
                        DataBean.setCurCar(carCur);
                        ShowCars.this.startActivity(i);
                    }else{
                        Toast.makeText(ShowCars.this,"User already has a job", Toast.LENGTH_LONG).show();
                        return;
                    }
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
                        carList.sort(new Comparator<CarCurrent>() {
                            @Override
                            public int compare(CarCurrent o1, CarCurrent o2) {
                                return o2.getFuelLevel() - o1.getFuelLevel();
                            }
                        });
                        lv.setAdapter(updateList(carList));
                    } else if ("Distance".equals(sel)) {
                        carList.sort(new Comparator<CarCurrent>() {
                            @Override
                            public int compare(CarCurrent o1, CarCurrent o2) {
                                if (distance(DataBean.getUserPosition().latitude, DataBean.getUserPosition().longitude, o1.getLat().doubleValue(),
                                        o1.getLng().doubleValue()) < distance(DataBean.getUserPosition().latitude, DataBean.getUserPosition().longitude,
                                        o2.getLat().doubleValue(), o2.getLng().doubleValue())) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                        lv.setAdapter(updateList(carList));
                    } else if ("Idletime".equals(sel)) {
                        carList.sort(new Comparator<CarCurrent>() {
                            @Override
                            public int compare(CarCurrent o1, CarCurrent o2) {
                                Long idleLong1 = Instant.now().getMillis() - o1.getCarCurrentPK().getTimestamp().getTime();
                                Long idleLong2 = Instant.now().getMillis() - o2.getCarCurrentPK().getTimestamp().getTime();
                                return idleLong1.intValue() - idleLong2.intValue();
                            }
                        });
                        lv.setAdapter(updateList(carList));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            Log.e(TAG,"onCreate DataBean carList is null");
        }
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
