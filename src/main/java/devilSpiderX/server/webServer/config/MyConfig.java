package devilSpiderX.server.webServer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "devilspiderx")
public class MyConfig {
    private final Path LOCAL_PATH = Paths.get(".").toAbsolutePath();
    private Path STATIC_LOCATION;
    private Map<String, String> V2RAY;

    public Path getLocalPath() {
        return LOCAL_PATH;
    }

    public Path getSTATIC_LOCATION() {
        return STATIC_LOCATION;
    }

    public void setSTATIC_LOCATION(String STATIC_LOCATION) {
        this.STATIC_LOCATION = Paths.get(STATIC_LOCATION).toAbsolutePath();
    }

    public Map<String, String> getV2RAY() {
        return V2RAY;
    }

    public void setV2RAY(Map<String, String> V2RAY) {
        this.V2RAY = V2RAY;
    }
}
