package devilSpiderX.server.webServer.server.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Configuration
@ConfigurationProperties(prefix = "devilspiderx")
public class DSXProperties {

    private final Path LOCAL_PATH = Paths.get(".")
            .toAbsolutePath();

    private V2rayProperties v2ray;

    private String avatarDirPath = "web/avatarImages";

}
