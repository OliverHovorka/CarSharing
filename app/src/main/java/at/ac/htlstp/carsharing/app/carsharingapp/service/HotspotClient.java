/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;

import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.model.Hotspot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * REST Web Service
 *
 * @author max
 */
public interface HotspotClient {

    @GET("hotspot")
    Call<List<Hotspot>> getHotspots();

    @GET("hotspot/{lat}/{lng}")
    Call<List<Hotspot>> getClosestHotSpot(@Path("lat") double lat, @Path("lng") double lng);

}
