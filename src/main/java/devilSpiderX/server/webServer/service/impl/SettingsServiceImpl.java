package devilSpiderX.server.webServer.service.impl;

import devilSpiderX.server.webServer.entity.Settings;
import devilSpiderX.server.webServer.service.SettingsService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.LinkedList;
import java.util.List;

@Service("settingsService")
public class SettingsServiceImpl implements SettingsService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    public SettingsServiceImpl() {
        List<Settings> insertList = new LinkedList<>();
        if (!exist("session_max_age")) {
            Settings settings = new Settings();
            settings.setKey("session_max_age");
            settings.setValue("600");
            insertList.add(settings);
        }
        if (!exist("page_size")) {
            Settings settings = new Settings();
            settings.setKey("page_size");
            settings.setValue("20");
            insertList.add(settings);
        }
        int n = suid.insert(insertList);
        LoggerFactory.getLogger(SettingsServiceImpl.class).info("初始化设置个数：{}", Math.max(n, 0));
    }

    @Override
    public String get(int id) {
        Settings setting = suid.selectById(new Settings(), id);
        if (setting == null) {
            return null;
        }
        return setting.getValue();
    }

    @Override
    public String get(String key) {
        Settings settings = new Settings();
        settings.setKey(key);
        List<Settings> resultList = suid.select(settings);
        if (resultList.isEmpty()) {
            return null;
        }
        settings = resultList.get(0);
        return settings.getValue();
    }

    @Override
    public List<Settings> getAll() {
        return suid.select(new Settings());
    }

    @Override
    public boolean set(int id, String value) {
        if (exist(id)) {
            Settings settings = new Settings();
            settings.setId(id);
            settings.setValue(value);
            int n = suid.updateById(settings, new ConditionImpl().setIncludeType(IncludeType.INCLUDE_EMPTY));
            return n > 0;
        }
        return false;
    }

    @Override
    public boolean set(String key, String value) {
        Settings settings = new Settings();
        settings.setKey(key);
        settings.setValue(value);
        int n;
        if (exist(key)) {
            n = suid.updateBy(settings, "key", IncludeType.INCLUDE_EMPTY);
        } else {
            n = suid.insert(settings, IncludeType.INCLUDE_EMPTY);
        }
        return n > 0;
    }

    @Override
    public boolean exist(int id) {
        Settings settings = new Settings();
        settings.setId(id);
        return suid.exist(settings);
    }

    @Override
    public boolean exist(String key) {
        Settings settings = new Settings();
        settings.setKey(key);
        return suid.exist(settings);
    }
}
