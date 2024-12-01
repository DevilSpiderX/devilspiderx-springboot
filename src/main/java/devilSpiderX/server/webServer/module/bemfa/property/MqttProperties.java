package devilSpiderX.server.webServer.module.bemfa.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mqtt.bemfa")
public class MqttProperties {
    private String url = "tcp://bemfa.com:9501";
    private String topic;
    private String clientId;
    private int maxReconnectCount = 5;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getMaxReconnectCount() {
        return maxReconnectCount;
    }

    public void setMaxReconnectCount(final int maxReconnectCount) {
        this.maxReconnectCount = maxReconnectCount;
    }
}
