/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Heinzl
 */

public class CarCurrent implements Serializable {

    private static final long serialVersionUID = 1L;
    protected CarCurrentPK carCurrentPK;
    private Integer fuelLevel;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private Car car;

    public CarCurrent() {
    }

    public CarCurrent(CarCurrentPK carCurrentPK) {
        this.carCurrentPK = carCurrentPK;
    }

    public CarCurrent(String vin, Date timestamp) {
        this.carCurrentPK = new CarCurrentPK(vin, timestamp);
    }

    public CarCurrentPK getCarCurrentPK() {
        return carCurrentPK;
    }

    public void setCarCurrentPK(CarCurrentPK carCurrentPK) {
        this.carCurrentPK = carCurrentPK;
    }

    public Integer getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Integer fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public long getHourDifference() {
        long seconds = (new Date().getTime() - carCurrentPK.getTimestamp().getTime()) / 1_000;
        return seconds / 3_600;
    }

    public long getMinuteDifference() {
        long seconds = (new Date().getTime() - carCurrentPK.getTimestamp().getTime()) / 1_000;
        return (seconds % 3_600) / 60;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carCurrentPK != null ? carCurrentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CarCurrent)) {
            return false;
        }
        CarCurrent other = (CarCurrent) object;
        if ((this.carCurrentPK == null && other.carCurrentPK != null) || (this.carCurrentPK != null && !this.carCurrentPK.equals(other.carCurrentPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vin: " + car.getVin() + " Model: " + car.getModel() + "\n Platenumber: " + car.getPlateNumber() + " Fueltype: " + car.getFuelType().getType();
    }

}
