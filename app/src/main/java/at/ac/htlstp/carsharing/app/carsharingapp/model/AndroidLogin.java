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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.successful ? 1 : 0);
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
        final AndroidLogin other = (AndroidLogin) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return Objects.equals(this.currentJob, other.currentJob);
    }

    @Override
    public String toString() {
        return "AndroidLogin{" + "successful=" + successful + ", user=" + user + ", currentJob=" + currentJob + '}';
    }

}