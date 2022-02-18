package devilSpiderX.server.webServer.service;

import java.io.IOException;

public class V2ray {
    private static final String exePath = "C:\\MyDownloads\\v2ray - Server\\v2ray-windows-64\\v2ray.exe";
    private static final String configPath = "C:\\MyDownloads\\v2ray - Server\\v2ray-windows-64\\config.json";
    private static boolean alive = false;
    private static Process p = null;

    public static long start() throws IOException {
        if (!alive) {
            p = Runtime.getRuntime().exec(String.format("\"%s\" \"-config=%s\"", exePath, configPath));
            //p = Runtime.getRuntime().exec("\"" + exePath + "\" \"-config=" + configPath + "\"");
            alive = true;
        }
        return p.pid();
    }

    public static String stop() {
        String result;
        if (alive) {
            result = OS.system("TASKKILL /F /T /PID " + p.pid());
            alive = false;
        } else {
            result = "v2ray不在运行中";
        }
        return result;
    }

    public static boolean isAlive() {
        return alive;
    }

}

