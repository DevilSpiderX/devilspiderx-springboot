package devilSpiderX.server.webServer.service.information;

import devilSpiderX.server.webServer.util.Arithmetic;

import java.text.DecimalFormat;

@SuppressWarnings("UnusedReturnValue")
public class Disk {
    /**
     * 磁盘标签
     */
    private String label;
    /**
     * 挂载点
     */
    private String mount;
    /**
     * 文件系统类型
     */
    private String fSType;
    /**
     * 文件类型
     */
    private String name;
    /**
     * 总大小（单位 B）
     */
    private long total;
    /**
     * 剩余大小（单位 B）
     */
    private long free;
    /**
     * 已经使用量（单位 B）
     */
    private long used;

    public String getLabel() {
        return label;
    }

    public Disk setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getMount() {
        return mount;
    }

    public Disk setMount(String mount) {
        this.mount = mount;
        return this;
    }

    public String getFSType() {
        return fSType;
    }

    public Disk setFSType(String fSType) {
        this.fSType = fSType;
        return this;
    }

    public String getName() {
        return name;
    }

    public Disk setName(String name) {
        this.name = name;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public Disk setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getFree() {
        return free;
    }

    public Disk setFree(long free) {
        this.free = free;
        return this;
    }

    public long getUsed() {
        return used;
    }

    public Disk setUsed(long used) {
        this.used = used;
        return this;
    }

    public String getTotalStr() {
        int KB = 1024;
        int MB = 1024 * 1024;
        int GB = 1024 * 1024 * 1024;
        if (total >= GB) {
            return Arithmetic.div(total, GB, 2) + " GB";
        } else if (total >= MB) {
            return Arithmetic.div(total, MB, 2) + " MB";
        } else if (total >= KB) {
            return Arithmetic.div(total, KB, 2) + " KB";
        } else {
            return new DecimalFormat("#.##").format(total) + " B";
        }
    }

    public String getFreeStr() {
        int KB = 1024;
        int MB = 1024 * 1024;
        int GB = 1024 * 1024 * 1024;
        if (free >= GB) {
            return Arithmetic.div(free, GB, 2) + " GB";
        } else if (free >= MB) {
            return Arithmetic.div(free, MB, 2) + " MB";
        } else if (free >= KB) {
            return Arithmetic.div(free, KB, 2) + " KB";
        } else {
            return new DecimalFormat("#.##").format(free) + " B";
        }
    }

    public String getUsedStr() {
        int KB = 1024;
        int MB = 1024 * 1024;
        int GB = 1024 * 1024 * 1024;
        if (used >= GB) {
            return Arithmetic.div(used, GB, 2) + " GB";
        } else if (used >= MB) {
            return Arithmetic.div(used, MB, 2) + " MB";
        } else if (used >= KB) {
            return Arithmetic.div(used, KB, 2) + " KB";
        } else {
            return new DecimalFormat("#.##").format(used) + " B";
        }
    }

    public String getUsage() {
        return new DecimalFormat("#.##%").format((double) used / total);
    }

    @Override
    public String toString() {
        return "Disk{" +
                "label='" + label + '\'' +
                ", dir='" + mount + '\'' +
                ", fSType='" + fSType + '\'' +
                ", name='" + name + '\'' +
                ", total=" + total +
                ", free=" + free +
                ", used=" + used +
                '}';
    }
}
