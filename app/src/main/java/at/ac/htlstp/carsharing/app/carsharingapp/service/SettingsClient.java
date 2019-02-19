/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * REST Web Service
 *
 * @author Heinzl
 */
public interface SettingsClient {

    @GET("settings/{key}")
    Call<String> getValue(@Path("key") String settingsKey);

    @POST("settings/{key}/{value}")
    Call<Boolean> setValue(@Path("key") String settingsKey, @Path("value") String value);

}
