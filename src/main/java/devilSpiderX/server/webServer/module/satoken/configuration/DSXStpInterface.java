package devilSpiderX.server.webServer.module.satoken.configuration;

import cn.dev33.satoken.stp.StpInterface;
import devilSpiderX.server.webServer.module.satoken.StpKit;
import devilSpiderX.server.webServer.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DSXStpInterface implements StpInterface {

    private final UserService userService;


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginType.equals(StpKit.USER_TYPE) && loginId instanceof Long uid) {
            return userService.getUserPermissions(uid);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginType.equals(StpKit.USER_TYPE) && loginId instanceof Long uid) {
            final List<String> list = new ArrayList<>();
            list.add("user");
            if (userService.isAdmin(uid)) {
                list.add("admin");
            }
            return list;
        }
        return Collections.emptyList();
    }
}
