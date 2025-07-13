package devilSpiderX.server.webServer.common.module.serverInfo.statistic;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CPU {
    /**
     * CPU名
     */
    private String name;
    /**
     * CPU物理核心数
     */
    private int physicalNum;
    /**
     * CPU逻辑核心数
     */
    private int logicalNum;
    /**
     * CPU使用率
     */
    private double usedRate;
    /**
     * 是否是64位处理器
     */
    private boolean a64bit;
    /**
     * CPU温度
     */
    private double temperature;
}
