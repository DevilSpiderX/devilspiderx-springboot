package devilSpiderX.server.webServer.server.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "devilspiderx.v2ray")
public class V2rayProperties {

    private String exePath = "v2ray";

    private String configPath = "";

}
