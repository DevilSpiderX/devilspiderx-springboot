package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.properties.DSXProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service(value = "v2ray")
public class V2ray {
    private final String exePath;
    private final String configPath;
    private boolean alive = false;
    private Process p = null;

    public V2ray(DSXProperties config) {
        exePath = config.getV2ray().getExePath();
        configPath = config.getV2ray().getConfigPath();
    }

    public long start() throws IOException {
        if (!alive) {
            ProcessBuilder builder = new ProcessBuilder(exePath, "-config", configPath);
            builder.redirectErrorStream(true);
            p = builder.start();
            alive = true;
        }
        return p.pid();
    }

    public String stop() {
        String result;
        if (alive) {
            p.destroy();
            alive = false;
            result = "v2ray关闭成功";
        } else {
            result = "v2ray不在运行中";
        }
        return result;
    }

    public boolean isAlive() {
        return alive;
    }
}

