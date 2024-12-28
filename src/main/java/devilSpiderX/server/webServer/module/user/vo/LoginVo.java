package devilSpiderX.server.webServer.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

@Schema(description = "登录状态Vo")
public record LoginVo(
        @Schema(description = "登录用户返回状态：0成功；1密码错误；2用户不存在；")
        int status,
        @Schema(description = "错误原因")
        @Nonnull
        String reason,
        @Schema(nullable = true)
        @Nullable
        LoginDataVo data
) {
    public LoginVo {
        Objects.requireNonNull(reason, "错误原因不能为空");
    }

    public static LoginVo of(int status, String reason) {
        return new LoginVo(status, reason, null);
    }

    public static LoginVo of(int status, String reason, @Nullable LoginDataVo data) {
        return new LoginVo(status, reason, data);
    }
}
