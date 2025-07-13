package devilSpiderX.server.webServer.common.module.serverInfo.statistic;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CurrentOS {
    /**
     * 操作系统名
     */
    private String name;
    /**
     * 操作系统位数
     */
    private int bitness;

    /**
     * 进程数量
     */
    private int processCount;
}
