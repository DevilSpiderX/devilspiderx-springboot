package devilSpiderX.server.webServer.module.user.entity;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 551625785764168103L;

    private String uid;
    private String password;
    private Boolean admin;
    private String lastAddress;
    private String avatarPath;

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

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    public String toString() {
        return "User{" +
               "uid='" + uid + '\'' +
               ", password='" + password + '\'' +
               ", admin=" + admin +
               ", lastAddress='" + lastAddress + '\'' +
               ", avatarPath='" + avatarPath + '\'' +
               '}';
    }
}

