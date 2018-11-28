package at.ac.htlstp.carsharing.app.carsharing;

import java.util.Date;

public class Car {

    private String vin;
    private String car_model;
    private String plate_number;
    private String fuel_type;
    private int fuel_level;
    private Date timestamp;
    private Location location;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public int getFuel_level() {
        return fuel_level;
    }

    public void setFuel_level(int fuel_level) {
        this.fuel_level = fuel_level;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return getVin().equals(car.getVin());
    }

    @Override
    public int hashCode() {

        return getVin().hashCode();
    }
}
