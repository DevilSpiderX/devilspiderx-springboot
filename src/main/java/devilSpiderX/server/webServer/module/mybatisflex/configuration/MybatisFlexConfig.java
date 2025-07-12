package devilSpiderX.server.webServer.module.mybatisflex.configuration;

import cn.dev33.satoken.context.SaHolder;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import devilSpiderX.server.webServer.core.model.base.BaseAllEntity;
import devilSpiderX.server.webServer.core.model.base.BaseEntity;
import devilSpiderX.server.webServer.module.satoken.StpKit;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * 配置自动填充字段。
 * 继承了BaseEntity、BaseAllEntity的实体类，
 * 会自动填充createUser、updateUser、createTime、updateTime这几个字段
 *
 * @author DevilSpiderX
 */
@Configuration
public class MybatisFlexConfig {

    @Bean
    public MyBatisFlexCustomizer mybatisFlexCustomizer() {
        final var baseEntityListener = new BaseEntityListener();

        return (config) -> {
            config.registerInsertListener(
                    baseEntityListener,
                    BaseAllEntity.class,
                    BaseEntity.class
            );
            config.registerUpdateListener(
                    baseEntityListener,
                    BaseAllEntity.class,
                    BaseEntity.class
            );
        };
    }

    public static class BaseEntityListener implements InsertListener, UpdateListener {

        @Override
        public void onInsert(final Object entity) {
            final Long userId = getUserId();
            final var time = LocalDateTime.now();

            if (entity instanceof BaseAllEntity baseAllEntity) {
                baseAllEntity.setCreateUser(userId);
                baseAllEntity.setUpdateUser(userId);
                baseAllEntity.setCreateTime(time);
                baseAllEntity.setUpdateTime(time);
            } else if (entity instanceof BaseEntity baseEntity) {
                baseEntity.setCreateTime(time);
                baseEntity.setUpdateTime(time);
            }
        }

        @Override
        public void onUpdate(final Object entity) {
            final Long userId = getUserId();
            final var time = LocalDateTime.now();

            if (entity instanceof BaseAllEntity baseAllEntity) {
                baseAllEntity.setUpdateUser(userId);
                baseAllEntity.setUpdateTime(time);
            } else if (entity instanceof BaseEntity baseEntity) {
                baseEntity.setUpdateTime(time);
            }
        }

        private @Nullable Long getUserId() {
            final var saContext = SaHolder.getContext();
            if (!saContext.isValid()) {
                return null;
            }

            if (StpKit.ADMIN.isLogin()) {
                return StpKit.ADMIN.getLoginIdAsLong();
            }

            if (StpKit.USER.isLogin()) {
                return StpKit.USER.getLoginIdAsLong();
            }

            return null;
        }
    }

}
