package devilSpiderX.server.webServer.module.serverInfo.statistic;

import java.util.Arrays;

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

    public Network(String name,
                   String displayName,
                   String macAddr,
                   long timeStamp,
                   long bytesSent,
                   long bytesRecv,
                   String[] IPv4Addr,
                   String[] IPv6Addr) {
        this.name = name;
        this.displayName = displayName;
        this.macAddr = macAddr;
        this.timeStamp = timeStamp;
        this.bytesSent = bytesSent;
        this.bytesRecv = bytesRecv;
        this.IPv4Addr = IPv4Addr;
        this.IPv6Addr = IPv6Addr;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesRecv() {
        return bytesRecv;
    }

    public void setBytesRecv(long bytesRecv) {
        this.bytesRecv = bytesRecv;
    }

    public long getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(long uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String[] getIPv4Addr() {
        return IPv4Addr;
    }

    public void setIPv4Addr(String[] IPv4Addr) {
        this.IPv4Addr = IPv4Addr;
    }

    public String[] getIPv6Addr() {
        return IPv6Addr;
    }

    public void setIPv6Addr(String[] IPv6Addr) {
        this.IPv6Addr = IPv6Addr;
    }

    @Override
    public String toString() {
        return "Network{" +
               "name='" + name + '\'' +
               ", displayName='" + displayName + '\'' +
               ", macAddr='" + macAddr + '\'' +
               ", timeStamp=" + timeStamp +
               ", bytesSent=" + bytesSent +
               ", bytesRecv=" + bytesRecv +
               ", uploadSpeed=" + uploadSpeed +
               ", downloadSpeed=" + downloadSpeed +
               ", IPv4Addr=" + Arrays.toString(IPv4Addr) +
               ", IPv6Addr=" + Arrays.toString(IPv6Addr) +
               '}';
    }
}
