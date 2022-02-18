package devilSpiderX.server.webServer.sql;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1595929511542L;

    private String uid;
    private String password;

    public User() {
    }

    public User(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}