package at.ac.htlstp.carsharing.app.carsharingapp;


public class Car {

    private static final long serialVersionUID = 1L;
    private String vin;
    private String model;
    private String plateNumber;
    private FuelType fuelType;
    private Location location;

    public Car() {
    }

    public Car(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vin != null ? vin.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Car)) {
            return false;
        }
        Car other = (Car) object;
        if ((this.vin == null && other.vin != null) || (this.vin != null && !this.vin.equals(other.vin))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Car: " + getModel() + " " + getPlateNumber();
    }

}


