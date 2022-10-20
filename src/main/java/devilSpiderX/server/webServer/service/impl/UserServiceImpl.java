package devilSpiderX.server.webServer.service.impl;

import devilSpiderX.server.webServer.entity.User;
import devilSpiderX.server.webServer.service.UserService;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    @Override
    public User get(String uid) {
        List<User> list = suid.select(new User(uid));
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public boolean register(String uid, String password) {
        User user = new User();
        user.setUid(uid);
        user.setPassword(password);
        user.setAdmin(false);
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
}
