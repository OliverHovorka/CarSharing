package at.ac.htlstp.carsharing.app.carsharingapp.model;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Heinzl
 */
public class AndroidUserAndJob implements Serializable {

    private User user;
    private Job currentJob;

    public AndroidUserAndJob() {
    }

    public AndroidUserAndJob(User user, Job currentJob) {
        this.user = user;
        this.currentJob = currentJob;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.user);
        hash = 19 * hash + Objects.hashCode(this.currentJob);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AndroidUserAndJob other = (AndroidUserAndJob) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return Objects.equals(this.currentJob, other.currentJob);
    }

    @Override
    public String toString() {
        return "AndroidUserAndJob{" + "user=" + user + ", currentJob=" + currentJob + '}';
    }

}
