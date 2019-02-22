/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;

import java.math.BigDecimal;
import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.model.AndroidLogin;
import at.ac.htlstp.carsharing.app.carsharingapp.model.User;
import at.ac.htlstp.carsharing.app.carsharingapp.model.UserCurrent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * REST Web Service
 *
 * @author Heinzl
 */
public interface UserClient {

    @GET("user")
    Call<List<User>> getUsers();

    @GET("user/pos")
    Call<List<UserCurrent>> getUserPositions();

    @GET("user/{userid}")
    Call<User> getUser(@Path("userid") int userId);

    @GET("user/current/{userid}")
    Call<UserCurrent> getUserCurrent(@Path("userid") int userId);

    @GET("user/bymail/{email}")
    Call<User> getUser(@Path("email") String email);

    @GET("user/current/bymail/{email}")
    Call<UserCurrent> getUserCurrent(@Path("email") String email);

    @GET("user/{email}/{pwd}")
    Call<Boolean> checkLogin(@Path("email") String email, @Path("pwd") String pwd);

    @POST("user/create/")
    Call<Boolean> createUser(User u);

    @POST("user/updatePw/{url}/{pwd}")
    Call<Boolean> updateUserPassword(@Path("url") String url, @Path("pwd") String newPwd);

    @POST("user/updatePwNoReset/{userId}/{pwd}")
    Call<Boolean> updateUserPasswordWithoutReset(@Path("userId") int userId, @Path("pwd") String newPwd);

    @POST("user/initReset/{email}")
    Call<Boolean> initReset(@Path("email") String email);

    @POST("user/updateEmail/{userId}/{newEmail}")
    Call<Boolean> updateEmail(@Path("userId") int userId, @Path("newEmail") String newEmail);

    @POST("user/updateLoc/{userId}/{lat}/{lng}")
    Call<Boolean> updateUserPosition(@Path("userId") int userId, @Path("lat") BigDecimal lat, @Path("lng") BigDecimal lng);

    @POST("user/updateActivated/{userId}/{activated}")
    Call<Boolean> updateUserActivated(@Path("userId") int userId, @Path("activated") boolean activated);

    @GET("user/available")
    Call<List<UserCurrent>> getAvailableUsers();

    @GET("user/androidLogin/{email}/{pwd}")
    Call<AndroidLogin> checkAndroidLogin(@Path("email") String email, @Path("pwd") String pwd);
}
