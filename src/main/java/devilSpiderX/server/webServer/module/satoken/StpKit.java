package devilSpiderX.server.webServer.module.satoken;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * @author DevilSpiderX
 */
public class StpKit {
    public static final String USER_TYPE = StpUtil.TYPE;
    public static final String ADMIN_TYPE = "admin";

    public static final StpLogic USER = StpUtil.getStpLogic();
    public static final StpLogic ADMIN = new StpLogic(ADMIN_TYPE);

    private StpKit() {
    }

}
