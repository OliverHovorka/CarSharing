package at.ac.htlstp.carsharing.app.carsharingapp.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import at.ac.htlstp.carsharing.app.carsharingapp.activities.AssignCar;
import at.ac.htlstp.carsharing.app.carsharingapp.activities.CurTask;

import static android.content.ContentValues.TAG;

public class MyLocationListener implements LocationListener{

    private CurTask task;
    private AssignCar assCar;
    private boolean diff;

    public MyLocationListener(CurTask t){
        Log.e(TAG,"LocationListener angelegt");
        task = t;
        diff = true;
    }

    public MyLocationListener(AssignCar car){
        assCar = car;
        diff = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(diff) {
            Log.e(TAG, "LOCATION CHANGED CURTASK");
            task.testLog("LOCATION CHANGED CURTASK");
            task.handleNewLocation(location);
        }else{
            Log.e(TAG, "LOCATION CHANGED ASSIGNCAR");
            assCar.testLog("LOCATION CHANGED ASSIGNCAR");
            assCar.handleNewLocation(location);
        }
    }
    @Override
    public void onProviderDisabled(String arg0)
    {
        // Do something here if you would like to know when the provider is disabled by the user
    }

    @Override
    public void onProviderEnabled(String arg0)
    {
        // Do something here if you would like to know when the provider is enabled by the user
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2)
    {
        // Do something here if you would like to know when the provider status changes
    }
}