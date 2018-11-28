package at.ac.htlstp.carsharing.app.carsharing;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CarRessource {

    @GET("cars")
    Call<List<Car>> getCars();

    @GET("cars/{id}")
    Call<List<Car>> getCars(@Path("id") int id);

    @GET("car/{vin}")
    Call<Car> getCar(@Path("vin") int vin);

    @GET("cars/period/{start}/{end}")
    Call<List<Car>> getCarsByPeriod(@Path("start") String start, @Path("end") String end);

    @GET("cars/fuel/{fuel_level}")
    Call<List<Car>> getCarsByFuelLevel(@Path("fuel_level") int fuel);

    @GET("cars/fuel/{fuel_min}/{fuel_max}")
    Call<List<Car>> getCarsByFuelMinMax(@Path("fuel_min") int min, @Path("fuel_max") int max);

}
