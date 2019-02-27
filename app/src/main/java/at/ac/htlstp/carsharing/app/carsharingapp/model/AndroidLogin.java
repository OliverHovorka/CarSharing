/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Heinzl
 */
public class AndroidLogin implements Serializable {

    private boolean successful;
    private User user;
    private Job currentJob;
    private CarCurrent cc;
    private List<CarCurrent> cars;

    public AndroidLogin() {
    }

    public AndroidLogin(boolean successful, User user, Job currentJob, CarCurrent cc, List<CarCurrent> cars) {
        this.successful = successful;
        this.user = user;
        this.currentJob = currentJob;
        this.cc = cc;
        this.cars = cars;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
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

    public CarCurrent getCc() {
        return cc;
    }

    public void setCc(CarCurrent cc) {
        this.cc = cc;
    }

    public List<CarCurrent> getCars() {
        return cars;
    }

    public void setCars(List<CarCurrent> cars) {
        this.cars = cars;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.successful ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.user);
        hash = 29 * hash + Objects.hashCode(this.currentJob);
        hash = 29 * hash + Objects.hashCode(this.cc);
        hash = 29 * hash + Objects.hashCode(this.cars);
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
        final AndroidLogin other = (AndroidLogin) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.currentJob, other.currentJob)) {
            return false;
        }
        if (!Objects.equals(this.cc, other.cc)) {
            return false;
        }
        return Objects.equals(this.cars, other.cars);
    }

    @Override
    public String toString() {
        return "AndroidLogin{" + "successful=" + successful + ", user=" + user + ", currentJob=" + currentJob + ", cc=" + cc + ", cars=" + cars + '}';
    }


}