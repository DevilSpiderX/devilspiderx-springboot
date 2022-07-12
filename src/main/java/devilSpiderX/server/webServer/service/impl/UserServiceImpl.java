package devilSpiderX.server.webServer.service.impl;

import devilSpiderX.server.webServer.service.UserService;
import devilSpiderX.server.webServer.entity.User;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final SuidRich dao = BeeFactoryHelper.getSuidRich();

    @Override
    public boolean isAdmin(String uid) {
        if (uid == null) return false;
        User user = dao.select(new User(uid)).get(0);
        return user != null && user.getAdmin();
    }

    @Override
    public boolean exist(String uid) {
        if (uid == null) {
            return false;
        }
        User user = new User(uid);
        return dao.exist(user);
    }
}
