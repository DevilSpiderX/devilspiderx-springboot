package devilSpiderX.server.webServer.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "注册状态Vo")
public record RegisterVo(
        @Schema(description = "注册用户返回状态：0成功；1失败；2用户已存在；")
        int status,
        @Schema(description = "失败原因")
        @Nonnull
        String reason
) {
}
