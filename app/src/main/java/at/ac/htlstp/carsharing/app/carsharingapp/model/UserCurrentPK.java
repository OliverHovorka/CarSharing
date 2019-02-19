/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Heinzl
 */
public class UserCurrentPK implements Serializable {

    private int uid;
    private Date timestamp;

    public UserCurrentPK() {
    }

    public UserCurrentPK(int uid, Date timestamp) {
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
        hash += (int) uid;
        hash += (timestamp != null ? timestamp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserCurrentPK)) {
            return false;
        }
        UserCurrentPK other = (UserCurrentPK) object;
        if (this.uid != other.uid) {
            return false;
        }
        if ((this.timestamp == null && other.timestamp != null) || (this.timestamp != null && !this.timestamp.equals(other.timestamp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "at.ac.htlstp.carsharing.server.model.UserCurrentPK[ uid=" + uid + ", timestamp=" + timestamp + " ]";
    }

}
