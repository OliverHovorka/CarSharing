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

public class UserCurrent implements Serializable {

    private static final long serialVersionUID = 1L;
    protected UserCurrentPK userCurrentPK;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private User vuser;

    public UserCurrent() {
    }

    public UserCurrent(UserCurrentPK userCurrentPK) {
        this.userCurrentPK = userCurrentPK;
    }

    public UserCurrent(int uid, Date timestamp) {
        this.userCurrentPK = new UserCurrentPK(uid, timestamp);
    }

    public UserCurrentPK getUserCurrentPK() {
        return userCurrentPK;
    }

    public void setUserCurrentPK(UserCurrentPK userCurrentPK) {
        this.userCurrentPK = userCurrentPK;
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

    public User getVuser() {
        return vuser;
    }

    public void setVuser(User vuser) {
        this.vuser = vuser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userCurrentPK != null ? userCurrentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserCurrent)) {
            return false;
        }
        UserCurrent other = (UserCurrent) object;
        if ((this.userCurrentPK == null && other.userCurrentPK != null) || (this.userCurrentPK != null && !this.userCurrentPK.equals(other.userCurrentPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "at.ac.htlstp.carsharing.server.model.UserCurrent[ userCurrentPK=" + userCurrentPK + " ]";
    }

}
