package devilSpiderX.server.webServer.core.service;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.update.UpdateChain;
import devilSpiderX.server.webServer.core.model.setting.entity.Setting;
import devilSpiderX.server.webServer.core.model.setting.mapper.SettingMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static devilSpiderX.server.webServer.core.constant.SettingConstant.DEFAULT_SESSION_MAX_AGE;
import static devilSpiderX.server.webServer.core.constant.SettingConstant.SESSION_MAX_AGE_KEY;
import static devilSpiderX.server.webServer.core.model.setting.entity.table.SettingTableDef.SETTING;

@RequiredArgsConstructor
@Service("settingsService")
public class SettingService implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SettingService.class);

    private SettingService self;
    private final SettingMapper settingMapper;

    @Override
    public void setApplicationContext(final @Nonnull ApplicationContext context) throws BeansException {
        self = context.getBean(SettingService.class);
    }

    @PostConstruct
    private void initSettings() {
        final List<Setting> insertList = new LinkedList<>();
        {
            if (!exist(SESSION_MAX_AGE_KEY)) {
                final var setting = new Setting();
                setting.setKey(SESSION_MAX_AGE_KEY);
                setting.setValue(String.valueOf(DEFAULT_SESSION_MAX_AGE));
                insertList.add(setting);
            }
        }

        if (!CollectionUtils.isEmpty(insertList)) {
            final var n = Db.txWithResult(() -> settingMapper.insertBatchSelective(insertList));
            logger.info("初始化设置个数：{}", n);
        }
    }

    public int getSessionMaxAge() {
        final var settingsOpt = findByKey(SESSION_MAX_AGE_KEY);
        if (settingsOpt.isEmpty()) return DEFAULT_SESSION_MAX_AGE;

        final var sessionMaxAge = settingsOpt.get();
        try {
            return Integer.parseInt(sessionMaxAge.getValue());
        } catch (NumberFormatException e) {
            logger.error("系统设置(session_max_age)的值不是数字");
            self.setSessionMaxAge(DEFAULT_SESSION_MAX_AGE);
            return DEFAULT_SESSION_MAX_AGE;
        }
    }

    @Transactional
    public void setSessionMaxAge(int sessionMaxAge) {
        final var updateChain = UpdateChain.of(settingMapper)
                .set(SETTING.VALUE, String.valueOf(sessionMaxAge))
                .where(SETTING.KEY.eq(SESSION_MAX_AGE_KEY));

        final var flag = updateChain.update();
        if (flag) {
            logger.info("session_max_age设置为{}", sessionMaxAge);
        }
    }

    public @Nonnull List<Setting> getAll() {
        return QueryChain.of(settingMapper)
                .list();
    }

    public @Nonnull Optional<Setting> findByKey(final @Nonnull String key) {
        return QueryChain.of(settingMapper)
                .where(SETTING.KEY.eq(key))
                .oneOpt();
    }

    public boolean exist(final @Nonnull String key) {
        return QueryChain.of(settingMapper)
                .where(SETTING.KEY.eq(key))
                .exists();
    }

}