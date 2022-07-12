package devilSpiderX.server.webServer.entity;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -2043792648177787953L;

    private String uid;
    private String password;
    private Boolean admin;

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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                '}';
    }
}