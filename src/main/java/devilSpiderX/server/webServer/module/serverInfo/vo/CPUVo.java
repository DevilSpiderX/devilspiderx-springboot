package devilSpiderX.server.webServer.module.serverInfo.vo;

import org.jetbrains.annotations.NotNull;

public record CPUVo(
        @NotNull
        String name,
        int physicalNum,
        int logicalNum,
        double usedRate,
        boolean is64bit,
        double cpuTemperature
) {
}
