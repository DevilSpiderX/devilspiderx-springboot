package devilSpiderX.server.webServer.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@Schema(description = "修改密码请求参数")
public record UpdatePasswordRequest(
        @Schema(description = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        String oldPassword,
        @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        String newPassword
) {
    public UpdatePasswordRequest {
        Objects.requireNonNull(oldPassword, "旧密码不能为空");
        Objects.requireNonNull(newPassword, "新密码不能为空");
    }
}
