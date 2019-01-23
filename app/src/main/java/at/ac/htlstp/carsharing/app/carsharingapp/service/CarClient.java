/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;

import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * REST Web Service
 *
 * @author Heinzl
 */
public interface CarClient {
    
    @GET("car")
    Call<List<CarCurrent>> getCars();

    @GET("car/location/{locId}")
    Call<List<CarCurrent>> getCars(@Path("locId") int locId);

    @GET("car/{vin}")
    Call<CarCurrent> getCar(@Path("vin") String vin);

    @GET("car/{fuel_min}/{fuel_max}")
    Call<List<CarCurrent>> getCars(@Path("fuel_min") int min, @Path("fuel_max") int max);

}
