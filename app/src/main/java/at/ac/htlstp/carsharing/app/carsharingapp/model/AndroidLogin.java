/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.carsharing.app.carsharingapp.model;

import java.io.Serializable;
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

    public AndroidLogin() {
    }

    public AndroidLogin(boolean successful, User user, Job currentJob) {
        this.successful = successful;
        this.user = user;
        this.currentJob = currentJob;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AndroidLogin that = (AndroidLogin) o;
        return isSuccessful() == that.isSuccessful() &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getCurrentJob(), that.getCurrentJob()) &&
                Objects.equals(getCc(), that.getCc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccessful(), getUser(), getCurrentJob(), getCc());
    }

    @Override
    public String toString() {
        return "AndroidLogin{" +
                "successful=" + successful +
                ", user=" + user +
                ", currentJob=" + currentJob +
                ", cc=" + cc +
                '}';
    }
}