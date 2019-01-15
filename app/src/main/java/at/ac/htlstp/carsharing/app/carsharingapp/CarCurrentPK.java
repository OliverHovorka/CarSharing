package at.ac.htlstp.carsharing.app.carsharingapp;

import java.util.Date;

public class CarCurrentPK {
    private String vin;
    private Date timestamp;

    public CarCurrentPK() {
    }

    public CarCurrentPK(String vin, Date timestamp) {
        this.vin = vin;
        this.timestamp = timestamp;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += vin.hashCode();
        hash += (timestamp != null ? timestamp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CarCurrentPK)) {
            return false;
        }
        CarCurrentPK other = (CarCurrentPK) object;
        if (this.vin != other.vin) {
            return false;
        }
        if ((this.timestamp == null && other.timestamp != null) || (this.timestamp != null && !this.timestamp.equals(other.timestamp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "at.ac.htlstp.carsharing.server.model.CarCurrentPK[ vin=" + vin + ", timestamp=" + timestamp + " ]";
    }


}
