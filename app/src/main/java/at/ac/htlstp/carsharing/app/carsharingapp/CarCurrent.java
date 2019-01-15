package at.ac.htlstp.carsharing.app.carsharingapp;

import java.math.BigDecimal;
import java.util.Date;

public class CarCurrent {
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
        return "Car: " + this.getCar().getModel() + " " + this.getCar().getPlateNumber() + "; " + this.getFuelLevel();
    }

}
