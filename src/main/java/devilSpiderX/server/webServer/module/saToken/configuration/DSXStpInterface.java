package devilSpiderX.server.webServer.module.saToken.configuration;

import cn.dev33.satoken.stp.StpInterface;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DSXStpInterface implements StpInterface {
    private final UserService userService;

    public DSXStpInterface(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId instanceof String uid) {
            return userService.getUserPermissions(uid);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        if (loginId instanceof String _loginId) {
            list.add("user");
            if (userService.isAdmin(_loginId)) {
                list.add("admin");
            }
        }
        return list;
    }
}
