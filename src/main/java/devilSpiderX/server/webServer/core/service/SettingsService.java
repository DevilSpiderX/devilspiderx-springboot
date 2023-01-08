package devilSpiderX.server.webServer.core.service;

import devilSpiderX.server.webServer.core.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.util.LinkedList;
import java.util.List;

@Service("settingsService")
public class SettingsService {
    private final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();
    private long lastUpdateTime;
    private int sessionMaxAge;
    private int pageSize;

    public SettingsService() {
        lastUpdateTime = System.currentTimeMillis();
        List<Settings> insertList = new LinkedList<>();
        {
            Settings settings = new Settings();
            settings.setKey("session_max_age");
            Settings one = suid.selectOne(settings);
            if (one == null) {
                settings.setValue("600");
                sessionMaxAge = 600;
                insertList.add(settings);
            } else {
                sessionMaxAge = Integer.parseInt(one.getValue());
            }
        }
        {
            Settings settings = new Settings();
            settings.setKey("page_size");
            Settings one = suid.selectOne(settings);
            if (one == null) {
                settings.setValue("20");
                pageSize = 20;
                insertList.add(settings);
            } else {
                pageSize = Integer.parseInt(one.getValue());
            }
        }
        int n = suid.insert(insertList);
        logger.info("初始化设置个数：{}", Math.max(n, 0));
    }

    private void update() {
        long fixedDelay = 10 * 60 * 1000;
        if (System.currentTimeMillis() - lastUpdateTime <= fixedDelay) {
            return;
        }
        for (Settings settings : getAll()) {
            switch (settings.getKey()) {
                case "session_max_age" -> sessionMaxAge = Integer.parseInt(settings.getValue());
                case "page_size" -> pageSize = Integer.parseInt(settings.getValue());
            }
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    public int getSessionMaxAge() {
        update();
        return sessionMaxAge;
    }

    public void setSessionMaxAge(int sessionMaxAge) {
        Settings settings = new Settings();
        settings.setKey("session_max_age");
        settings.setValue(String.valueOf(sessionMaxAge));
        int n = suid.updateBy(settings, "key", IncludeType.EXCLUDE_BOTH);
        if (n > 0) {
            this.sessionMaxAge = sessionMaxAge;
            logger.info("session_max_age设置为{}", sessionMaxAge);
        }
    }

    public int getPageSize() {
        update();
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        Settings settings = new Settings();
        settings.setKey("page_size");
        settings.setValue(String.valueOf(pageSize));
        int n = suid.updateBy(settings, "key", IncludeType.EXCLUDE_BOTH);
        if (n > 0) {
            this.pageSize = pageSize;
            logger.info("page_size设置为{}", pageSize);
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
