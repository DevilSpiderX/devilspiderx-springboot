package devilSpiderX.server.webServer.module.serverInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "CPU信息Vo")
public record CPUVo(
        @Schema(description = "cpu型号")
        @Nonnull
        String name,
        @Schema(description = "物理核心数")
        int physicalNum,
        @Schema(description = "逻辑核心数")
        int logicalNum,
        @Schema(description = "使用率")
        double usedRate,
        @Schema(description = "是否64位cpu")
        boolean is64bit,
        @Schema(description = "温度")
        double cpuTemperature
) {
}
