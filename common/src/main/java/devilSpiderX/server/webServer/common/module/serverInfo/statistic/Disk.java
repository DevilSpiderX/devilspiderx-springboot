package devilSpiderX.server.webServer.common.module.serverInfo.statistic;

import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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

    @Override
    public int compareTo(@Nonnull Disk o) {
        return this.mount.compareTo(o.mount);
    }
}
