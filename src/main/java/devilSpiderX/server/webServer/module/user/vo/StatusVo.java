package devilSpiderX.server.webServer.module.user.vo;

import java.util.Collections;
import java.util.List;

public class StatusVo {
    private String uid;
    private boolean login = false;
    private boolean admin = false;
    private List<String> permissions = Collections.emptyList();

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(final boolean login) {
        this.login = login;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<String> permissions) {
        if (permissions == null) {
            return;
        }
        this.permissions = permissions;
    }
}
