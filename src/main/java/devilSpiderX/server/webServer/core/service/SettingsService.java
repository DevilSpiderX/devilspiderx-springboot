package devilSpiderX.server.webServer.core.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import devilSpiderX.server.webServer.core.dao.SettingsMapper;
import devilSpiderX.server.webServer.core.entity.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Service("settingsService")
public class SettingsService {
    public static final int DEFAULT_SESSION_MAX_AGE = 3600;

    private final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private final SettingsMapper settingsMapper;

    public SettingsService(SettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
        final List<Settings> insertList = new LinkedList<>();
        {
            if (!exist("session_max_age")) {
                final var settings = new Settings();
                settings.setKey("session_max_age");
                settings.setValue(String.valueOf(DEFAULT_SESSION_MAX_AGE));
                insertList.add(settings);
            }
        }

        if (!CollectionUtils.isEmpty(insertList)) {
            final int n = settingsMapper.insertAll(insertList);
            logger.info("初始化设置个数：{}", n);
        }
    }

    public int getSessionMaxAge() {
        final var settingsOpt = settingsMapper.findByKey("session_max_age");
        if (settingsOpt.isEmpty()) return DEFAULT_SESSION_MAX_AGE;

        final var sessionMaxAge = settingsOpt.get();
        try {
            return Integer.parseInt(sessionMaxAge.getValue());
        } catch (NumberFormatException e) {
            logger.error("系统设置(session_max_age)的值不是数字");
            setSessionMaxAge(DEFAULT_SESSION_MAX_AGE);
            return DEFAULT_SESSION_MAX_AGE;
        }
    }

    public void setSessionMaxAge(int sessionMaxAge) {
        int n = settingsMapper.updateByKey("session_max_age", String.valueOf(sessionMaxAge));
        if (n > 0) {
            logger.info("session_max_age设置为{}", sessionMaxAge);
        }
    }

    public List<Settings> getAll() {
        return settingsMapper.findAll();
    }

    public boolean exist(String key) {
        return settingsMapper.exists(
                Wrappers.lambdaQuery(Settings.class)
                        .eq(Settings::getKey, key)
        );
    }
}