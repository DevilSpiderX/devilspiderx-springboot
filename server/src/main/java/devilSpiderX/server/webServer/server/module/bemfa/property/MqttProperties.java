package devilSpiderX.server.webServer.server.module.bemfa.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt.bemfa")
public class MqttProperties {

    private String url = "tcp://bemfa.com:9501";

    private String topic;

    private String clientId;

}
