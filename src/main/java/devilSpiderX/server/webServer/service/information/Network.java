package devilSpiderX.server.webServer.service.information;

import java.util.Arrays;

public class Network {
    private final String name;
    private long timeStamp;
    private long bytesSent;
    private long bytesRecv;
    /**
     * 上传速度 B/s
     */
    private long updateSpeed;
    /**
     * 下载速度 B/s
     */
    private long downloadSpeed;

    private String[] IPv4addr;

    private String[] IPv6addr;

    public Network(String name, long timeStamp, long bytesSent, long bytesRecv) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.bytesSent = bytesSent;
        this.bytesRecv = bytesRecv;
    }

    public String getName() {
        return name;
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

    public long getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateSpeed(long updateSpeed) {
        this.updateSpeed = updateSpeed;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String[] getIPv4addr() {
        return IPv4addr;
    }

    public void setIPv4addr(String[] IPv4addr) {
        this.IPv4addr = IPv4addr;
    }

    public String[] getIPv6addr() {
        return IPv6addr;
    }

    public void setIPv6addr(String[] IPv6addr) {
        this.IPv6addr = IPv6addr;
    }

    @Override
    public String toString() {
        return "Network{" +
                "name='" + name + '\'' +
                ", timeStamp=" + timeStamp +
                ", bytesSent=" + bytesSent +
                ", bytesRecv=" + bytesRecv +
                ", updateSpeed=" + updateSpeed +
                ", downloadSpeed=" + downloadSpeed +
                ", IPv4addr=" + Arrays.toString(IPv4addr) +
                ", IPv6addr=" + Arrays.toString(IPv6addr) +
                '}';
    }
}
