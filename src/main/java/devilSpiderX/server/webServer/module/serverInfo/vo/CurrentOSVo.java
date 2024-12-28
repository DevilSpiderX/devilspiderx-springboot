package devilSpiderX.server.webServer.module.serverInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "当前系统信息Vo")
public record CurrentOSVo(
        @Schema(description = "系统名")
        @Nonnull
        String name,
        @Schema(description = "系统位数", examples = {"32", "64"})
        int bitness,
        @Schema(description = "系统正在运行的进程数")
        int processCount
) {
}
