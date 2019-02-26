
package at.ac.htlstp.carsharing.app.carsharingapp.model;


import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author max
 */
public class Token implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private User uid;

    public Token() {
    }

    public Token(String token, User userId) {
        this.token = token;
        this.uid = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUid() {
        return uid;
    }

    public void setUid(User uid) {
        this.uid = uid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Token other = (Token) obj;
        return Objects.equals(this.token, other.token);
    }

}

