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

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;
import at.ac.htlstp.carsharing.app.carsharingapp.service.CarClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.DataBean;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.JobClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrevTasks extends AppCompatActivity {
    public static final String TAG = PrevTasks.class.getSimpleName();
    private static List<Job> jobList = new ArrayList<>();

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.prev_jobs);
        Window w = this.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.car2goBlue));
        if (!DataBean.getJobList().isEmpty()) {
            JobClient client = GenericService.getClient(JobClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            Call<List<Job>> callJobList = client.getFinishedJobsForUser(DataBean.getUser().getId());
            callJobList.enqueue(new Callback<List<Job>>() {
                @Override
                public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                    if (response.body() != null) {
                        DataBean.setJobList(response.body());
                    } else {
                        Log.e(TAG, "Call body null");
                    }
                }

                @Override
                public void onFailure(Call<List<Job>> call, Throwable t) {
                    Log.e(TAG, "JobCall failed");
                }
            });
        } else {
            jobList = DataBean.getJobList();
        }
        ArrayAdapter<Job> listAdapterLV = new ArrayAdapter<Job>(PrevTasks.this, R.layout.simplerow, jobList) {
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
        final ListView lv = (ListView) findViewById(R.id.jobList);
        lv.setAdapter(listAdapterLV);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(PrevTasks.this, AssignCar.class);
                Object listItem = lv.getItemAtPosition(position);
                CarCurrent carCur = (CarCurrent) listItem;
                DataBean.setCurCar(carCur);
                PrevTasks.this.startActivity(i);
            }
        });

    }
}
