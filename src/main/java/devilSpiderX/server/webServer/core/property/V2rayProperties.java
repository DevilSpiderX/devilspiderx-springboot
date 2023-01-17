package devilSpiderX.server.webServer.core.property;

public class V2rayProperties {
    private String exePath = "v2ray";
    private String configPath = "";

    public String getExePath() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath = exePath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
