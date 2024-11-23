package devilSpiderX.server.webServer.module.user.entity;

import java.io.Serial;
import java.io.Serializable;


public class UserPermission implements Serializable {
    @Serial
    private static final long serialVersionUID = -21831749449358287L;

    private Integer id;
    private String uid;
    private String permission;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}

