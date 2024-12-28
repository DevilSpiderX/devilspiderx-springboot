package devilSpiderX.server.webServer.module.serverInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "硬盘分区信息Vo")
public record DiskVo(
        @Schema(description = "分区标签")
        @Nonnull
        String label,
        @Schema(description = "分区绑定的路径")
        @Nonnull
        String mount,
        @Schema(description = "文件系统类型")
        @Nonnull
        String fSType,
        @Schema(description = "分区名")
        @Nonnull
        String name,
        @Schema(description = "总大小")
        long total,
        @Schema(description = "可用大小")
        long free,
        @Schema(description = "已用大小")
        long used
) {
}
