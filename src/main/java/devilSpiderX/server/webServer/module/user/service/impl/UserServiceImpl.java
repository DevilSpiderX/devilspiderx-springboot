package devilSpiderX.server.webServer.module.user.service.impl;

import devilSpiderX.server.webServer.module.user.entity.User;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    @Override
    public User get(String uid) {
        if (uid == null) {
            return null;
        }
        List<User> list = suid.select(new User(uid));
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public boolean register(String uid, String password, String ipAddr) {
        User user = new User();
        user.setUid(uid);
        user.setPassword(password);
        user.setAdmin(false);
        user.setLastAddress(ipAddr);
        return suid.insert(user) > 0;
    }

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
        User user = suid.select(new User(uid)).get(0);
        return user != null && user.getAdmin();
    }

    @Override
    public boolean exist(String uid) {
        if (uid == null) {
            return false;
        }
        return suid.exist(new User(uid));
    }

    @Override
    public boolean updateLastAddr(String uid, String ipAddr) {
        if (uid == null || ipAddr == null) {
            return false;
        }
        User user = new User(uid);
        user.setLastAddress(ipAddr);
        int n = suid.updateBy(user, "uid", IncludeType.INCLUDE_EMPTY);
        return n > 0;
    }
}
