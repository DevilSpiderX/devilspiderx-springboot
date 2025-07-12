package devilSpiderX.server.webServer.module.satoken.properties;

import cn.dev33.satoken.config.SaTokenConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author DevilSpiderX
 */
@Component
@ConfigurationProperties(StpKitProperties.PREFIX)
public class StpKitProperties {
    public static final String PREFIX = "sa-token-kit";
    public static final String ADMIN_CONFIG_PREFIX = "sa-token-kit.admin";

    private final SaTokenConfig admin = new SaTokenConfig();

    @ConfigurationProperties(ADMIN_CONFIG_PREFIX)
    public SaTokenConfig getAdmin() {
        return admin;
    }
}
