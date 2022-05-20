package devilSpiderX.server.webServer.service.information;

import devilSpiderX.server.webServer.util.Arithmetic;

import java.text.DecimalFormat;

@SuppressWarnings("UnusedReturnValue")
public class Memory {
    /**
     * 内存总量
     */
    private long total;

    /**
     * 已用内存
     */
    private long used;

    /**
     * 剩余内存
     */
    private long free;

    public long getTotal() {
        return total;
    }

    public Memory setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getUsed() {
        return used;
    }

    public Memory setUsed(long used) {
        this.used = used;
        return this;
    }

    public long getFree() {
        return free;
    }

    public Memory setFree(long free) {
        this.free = free;
        return this;
    }

    public String getTotalStr() {
        return Arithmetic.div(total, (1024 * 1024 * 1024), 2) + " GB";
    }

    public String getUsedStr() {
        return Arithmetic.div(used, (1024 * 1024 * 1024), 2) + " GB";
    }

    public String getFreeStr() {
        return Arithmetic.div(free, (1024 * 1024 * 1024), 2) + " GB";
    }

    public String getUsage() {
        return new DecimalFormat("#.##%").format((double) used / total);
    }

    @Override
    public String toString() {
        return "Mem{" +
                "total=" + getTotal() +
                ", used=" + getUsed() +
                ", free=" + getFree() +
                '}';
    }
}

