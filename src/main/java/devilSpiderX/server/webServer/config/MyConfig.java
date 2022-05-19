package devilSpiderX.server.webServer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "my")
public class MyConfig {
    private final Path LOCAL_PATH = Paths.get(".").toAbsolutePath();
    private Path STATIC_LOCATION;

    public Path getLocalPath() {
        return LOCAL_PATH;
    }

    public Path getSTATIC_LOCATION() {
        return STATIC_LOCATION;
    }

    public void setSTATIC_LOCATION(String STATIC_LOCATION) {
        this.STATIC_LOCATION = Paths.get(STATIC_LOCATION).toAbsolutePath();
    }
}
