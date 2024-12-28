package devilSpiderX.server.webServer.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

@Schema(description = "用户状态Vo")
public class StatusVo {
    @Schema(description = "用户id")
    private String uid;
    @Schema(description = "是否登录")
    private boolean login = false;
    @Schema(description = "是否管理员")
    private boolean admin = false;
    @Schema(description = "用户的角色列表")
    private List<String> roles = Collections.emptyList();
    @Schema(description = "用户权限列表")
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(final List<String> roles) {
        if (roles == null) {
            return;
        }
        this.roles = roles;
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
