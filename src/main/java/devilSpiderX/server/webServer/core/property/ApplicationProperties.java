package devilSpiderX.server.webServer.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationProperties {

    private String name;

    private String version;

}
