package devilSpiderX.server.webServer.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "devilspiderx")
public class DSXProperties {
    private final Path LOCAL_PATH = Paths.get(".").toAbsolutePath();
    private V2rayProperties v2ray;
    private String pidFileName = "devilspiderx.pid";
    private String avatarDirPath = "web/avatarImages";

    public Path getLocalPath() {
        return LOCAL_PATH;
    }

    public V2rayProperties getV2ray() {
        return v2ray;
    }

    public void setV2ray(V2rayProperties v2ray) {
        this.v2ray = v2ray;
    }

    public String getPidFileName() {
        return pidFileName;
    }

    public void setPidFileName(String pidFileName) {
        this.pidFileName = pidFileName;
    }

    public String getAvatarDirPath() {
        return avatarDirPath;
    }

    public void setAvatarDirPath(String avatarDirPath) {
        this.avatarDirPath = avatarDirPath;
    }
}
