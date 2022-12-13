package devilSpiderX.server.webServer.module.serverInfo.statistics;

import org.jetbrains.annotations.NotNull;

public class Disk implements Comparable<Disk> {
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

    @Override
    public int compareTo(@NotNull Disk o) {
        return this.mount.compareTo(o.mount);
    }
}
