package devilSpiderX.server.webServer.common.module.serverInfo.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "内存信息Vo")
public record MemoryVO(
        @Schema(description = "总大小")
        long total,
        @Schema(description = "已用大小")
        long used,
        @Schema(description = "可用大小")
        long free
) {
}
