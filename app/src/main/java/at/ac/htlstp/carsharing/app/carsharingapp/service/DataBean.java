package at.ac.htlstp.carsharing.app.carsharingapp.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import at.ac.htlstp.carsharing.app.carsharingapp.model.CarCurrent;
import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;
import at.ac.htlstp.carsharing.app.carsharingapp.model.User;

public class DataBean {

    private static CarCurrent curCar = null;

    private static User user = null;

    private static Job curJob = null;

    private static List<CarCurrent> carList = new ArrayList<>();

    private static DataBean INSTANCE = new DataBean();

    private static LatLng userPosition= null;

    private DataBean(){

    }

    public static CarCurrent getCurCar() {
        return curCar;
    }

    public static void setCurCar(CarCurrent curCar) {
        DataBean.curCar = curCar;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        DataBean.user = user;
    }

    public static Job getCurJob() {
        return curJob;
    }

    public static void setCurJob(Job curJob) {
        DataBean.curJob = curJob;
    }

    public static DataBean getINSTANCE() {
        return INSTANCE;
    }

    public static List<CarCurrent> getCarList() {
        return carList;
    }

    public static void setCarList(List<CarCurrent> carList) {
        DataBean.carList = carList;
    }

    public static void addCar(CarCurrent cc){
        carList.add(cc);
    }

    public static void delCar(CarCurrent cc){
        carList.remove(cc);
    }

    public static LatLng getUserPosition() {
        return userPosition;
    }

    public static void setUserPosition(LatLng userPositiong) {
        DataBean.userPosition = userPositiong;
    }

    public static void reset(){
        curCar = null;
        user = null;
        curJob = null;
        carList = null;
        userPosition = null;
    }
}
