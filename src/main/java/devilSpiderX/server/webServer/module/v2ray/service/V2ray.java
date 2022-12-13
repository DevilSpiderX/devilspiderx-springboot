package devilSpiderX.server.webServer.module.v2ray.service;

import devilSpiderX.server.webServer.core.properties.DSXProperties;
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
            ProcessBuilder builder = new ProcessBuilder(exePath, "run", "-c", configPath);
            builder.redirectErrorStream(true);
            p = builder.start();
            alive = true;
        }
        return p.pid();
    }

    public boolean stop() {
        boolean result = false;
        if (alive) {
            p.destroy();
            alive = false;
            result = true;
        }
        return result;
    }

    public boolean isAlive() {
        return alive;
    }
}

