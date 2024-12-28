package devilSpiderX.server.webServer.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@Schema(description = "登录请求参数")
public record LoginRequest(
        @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        String uid,
        @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        String pwd
) {
    public LoginRequest {
        Objects.requireNonNull(uid, "必须存在用户id");
        Objects.requireNonNull(pwd, "必须存在密码");
    }

    /**
     * @return 密码
     */
    @Nonnull
    public String password() {
        return pwd;
    }
}
