package devilSpiderX.server.webServer.core.service;

import devilSpiderX.server.webServer.core.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.util.LinkedList;
import java.util.List;

@Service("settingsService")
@DependsOn("manageConfig")
public class SettingsService {
    private final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    public SettingsService() {
        final List<Settings> insertList = new LinkedList<>();
        {
            final Settings settings = new Settings();
            settings.setKey("session_max_age");
            if (!suid.exist(settings)) {
                settings.setValue("3600");
                insertList.add(settings);
            }
        }
        int n = suid.insert(insertList);
        logger.info("初始化设置个数：{}", Math.max(n, 0));
    }

    public int getSessionMaxAge() {
        Settings sessionMaxAge = suid.selectOne(new Settings("session_max_age"));
        try {
            return Integer.parseInt(sessionMaxAge.getValue());
        } catch (NumberFormatException e) {
            logger.error("系统设置(session_max_age)的值不是数字");
            return 3600;
        }
    }

    public void setSessionMaxAge(int sessionMaxAge) {
        Settings settings = new Settings();
        settings.setKey("session_max_age");
        settings.setValue(String.valueOf(sessionMaxAge));
        int n = suid.updateBy(settings, IncludeType.EXCLUDE_BOTH, "key");
        if (n > 0) {
            logger.info("session_max_age设置为{}", sessionMaxAge);
        }
    }

    public List<Settings> getAll() {
        return suid.select(new Settings());
    }

    public boolean exist(String key) {
        Settings settings = new Settings();
        settings.setKey(key);
        return suid.exist(settings);
    }
}
