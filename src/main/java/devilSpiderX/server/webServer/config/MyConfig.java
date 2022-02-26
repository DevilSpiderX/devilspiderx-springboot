package devilSpiderX.server.webServer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "my")
public class MyConfig {
    private final String LOCAL_PATH = Paths.get(".").toAbsolutePath().toString();
    private String STATIC_LOCATION;
    private boolean TRAY_ICON;
    private int TRAY_ICON_INDEX;

    public String getLocalPath() {
        return LOCAL_PATH;
    }

    public String getSTATIC_LOCATION() {
        return STATIC_LOCATION;
    }

    public void setSTATIC_LOCATION(String STATIC_LOCATION) {
        this.STATIC_LOCATION = Paths.get(STATIC_LOCATION).toAbsolutePath().toString();
    }

    public boolean getTRAY_ICON() {
        return TRAY_ICON;
    }

    public void setTRAY_ICON(boolean TRAY_ICON) {
        this.TRAY_ICON = TRAY_ICON;
    }

    public int getTRAY_ICON_INDEX() {
        return TRAY_ICON_INDEX;
    }

    public void setTRAY_ICON_INDEX(int TRAY_ICON_INDEX) {
        this.TRAY_ICON_INDEX = TRAY_ICON_INDEX;
    }


}
