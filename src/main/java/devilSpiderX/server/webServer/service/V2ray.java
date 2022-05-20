package devilSpiderX.server.webServer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class V2ray {
    private static final String exePath;
    private static final String configPath;
    private static boolean alive = false;
    private static Process p = null;

    static {
        Logger logger = LoggerFactory.getLogger(V2ray.class);
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream("./v2rayParams.json");
            JSONObject json = JSON.parseObject(fileIn, StandardCharsets.UTF_8, JSONObject.class);
            exePath = json.getString("exe-path");
            configPath = json.getString("config-path");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static long start() throws IOException {
        if (!alive) {
            ProcessBuilder builder = new ProcessBuilder(exePath, "-config=" + configPath);
            builder.redirectErrorStream(true);
            p = builder.start();
            alive = true;
        }
        return p.pid();
    }

    public static String stop() {
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

    public static boolean isAlive() {
        return alive;
    }
}

