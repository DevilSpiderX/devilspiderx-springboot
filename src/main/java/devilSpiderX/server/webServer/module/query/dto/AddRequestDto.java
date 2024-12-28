package devilSpiderX.server.webServer.module.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "添加密码记录请求参数")
public record AddRequestDto(
        @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        String name,
        @Schema(description = "账号")
        String account,
        @Schema(description = "密码")
        String password,
        @Schema(description = "备注")
        String remark) {
}
