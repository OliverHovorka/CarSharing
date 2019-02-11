/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;


import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * REST Web Service
 *
 * @author Heinzl
 */
public interface JobClient {

    @GET("job/{userId}")
    Call<List<Job>> getJobsForUser(@Path("userId") int id);

    @GET("job/finished/{userId}")
    Call<List<Job>> getFinishedJobsForUser(@Path("userId") int id);

    @GET("job/current/{userId}")
    Call<Job> getCurrentJobForUser(@Path("userId") int id);

    @POST("job/create/{userId}/{vin}")
    Call<Boolean> createJob(@Path("userId") int uid, @Path("vin") String vin);

    @POST("job/finish/{jobId}")
    Call<Boolean> finishJob(@Path("jobId") int jid);

}
