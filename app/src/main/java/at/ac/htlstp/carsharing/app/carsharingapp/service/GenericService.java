/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author Heinzl
 */
public class GenericService {

    private static final Map<Class<?>, Object> CLIENT_CACHE = new HashMap<>();

    /*
     * Diese Methode generiert einen Client durch welchen mit dem REST Endpunkt kommuniziert werden kann
     * Diese Funktion ist generisch da im Projekt CarSharing diverse Clients in verwendung sind
     * Es wird hier ebenfalls ziwschen den Clients der Android Applikation und der Website unterschieden mit Hilfe von keys
     */
    public static <T> T getClient(Class<T> cls, final String key) {
        synchronized (CLIENT_CACHE) {
            if (CLIENT_CACHE.containsKey(cls)) {
                return (T) CLIENT_CACHE.get(cls);
            }
        }

        String API_BASE_URL = "https://carsharing.privatevoid.net/server/rest/";
        Gson gson = new GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        /*
         * Hier wird der HTTPClient kreiert
         */
        OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request r = chain.request();
                HttpUrl url = r.url().newBuilder().addQueryParameter("key", key).build();
                r = r.newBuilder().url(url).build();
                return chain.proceed(r);
            }
        }).build();

        /*
         * Hier wird nun die URL festgelegt unter wlcher der REST Endpunkt Erreichbar ist.
         * Weiters wird hier der Converter angegeben
         */
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(okClient).build();

        T client = retrofit.create(cls);

        synchronized (CLIENT_CACHE) {
            CLIENT_CACHE.put(cls, client);
        }

        return client;
    }

}
