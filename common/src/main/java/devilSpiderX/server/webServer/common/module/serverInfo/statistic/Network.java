package devilSpiderX.server.webServer.common.module.serverInfo.statistic;

import lombok.Data;

@Data
public class Network {
    private final String name;
    private final String displayName;
    private final String macAddr;
    private long timeStamp;
    private long bytesSent;
    private long bytesRecv;
    /**
     * 上传速度 B/s
     */
    private long uploadSpeed;
    /**
     * 下载速度 B/s
     */
    private long downloadSpeed;

    private String[] IPv4Addr;

    private String[] IPv6Addr;

    public Network(String name) {
        this.name = name;
        this.displayName = null;
        this.macAddr = null;
        this.timeStamp = System.currentTimeMillis();
    }

    public Network(
            String name,
            String displayName,
            String macAddr,
            long timeStamp,
            long bytesSent,
            long bytesRecv,
            String[] IPv4Addr,
            String[] IPv6Addr
    ) {
        this.name = name;
        this.displayName = displayName;
        this.macAddr = macAddr;
        this.timeStamp = timeStamp;
        this.bytesSent = bytesSent;
        this.bytesRecv = bytesRecv;
        this.IPv4Addr = IPv4Addr;
        this.IPv6Addr = IPv6Addr;
    }
}
