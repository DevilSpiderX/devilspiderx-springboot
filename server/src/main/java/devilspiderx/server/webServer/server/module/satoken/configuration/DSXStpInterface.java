package devilSpiderX.server.webServer.server.module.satoken.configuration;

import cn.dev33.satoken.stp.StpInterface;
import devilSpiderX.server.webServer.server.module.satoken.StpKit;
import devilSpiderX.server.webServer.server.module.user.service.UserService;
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
        final long id = Long.parseLong(loginId.toString());
        if (loginType.equals(StpKit.USER_TYPE)) {
            return userService.getUserPermissions(id);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        final long id = Long.parseLong(loginId.toString());
        if (loginType.equals(StpKit.USER_TYPE)) {
            final List<String> list = new ArrayList<>();
            list.add("user");
            if (userService.isAdmin(id)) {
                list.add("admin");
            }
            return list;
        }
        return Collections.emptyList();
    }
}
