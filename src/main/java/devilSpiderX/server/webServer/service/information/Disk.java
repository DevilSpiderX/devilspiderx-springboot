package devilSpiderX.server.webServer.service.information;

import devilSpiderX.server.webServer.util.Arithmetic;

import java.text.DecimalFormat;

public class Disk {
    /**
     * 磁盘标签
     */
    private String label;
    /**
     * 盘符路径
     */
    private String dir;
    /**
     * 盘符类型
     */
    private String fSType;
    /**
     * 文件类型
     */
    private String name;
    /**
     * 总大小
     */
    private long total;
    /**
     * 剩余大小
     */
    private long free;
    /**
     * 已经使用量
     */
    private long used;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFSType() {
        return fSType;
    }

    public void setFSType(String fSType) {
        this.fSType = fSType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
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
                ", dir='" + dir + '\'' +
                ", fSType='" + fSType + '\'' +
                ", name='" + name + '\'' +
                ", total=" + total +
                ", free=" + free +
                ", used=" + used +
                '}';
    }
}
