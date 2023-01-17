package devilSpiderX.server.webServer.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private String title;
    private String version;

    public ApplicationProperties() {
        Package pkg = this.getClass().getPackage();
        title = pkg.getImplementationTitle();
        version = pkg.getImplementationVersion();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
