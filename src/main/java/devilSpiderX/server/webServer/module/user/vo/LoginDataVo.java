package devilSpiderX.server.webServer.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

import java.util.List;

@Schema(description = "用户登录数据Vo")
public record LoginDataVo(
        @Schema(description = "用户id")
        @Nonnull
        String uid,
        @Schema(description = "用户token")
        @Nonnull
        String token,
        @Schema(description = "是否管理员")
        boolean admin,
        @Schema(description = "用户的角色列表")
        @Nonnull
        List<String> roles,
        @Schema(description = "用户的权限列表")
        @Nonnull
        List<String> permissions,
        @Schema(description = "上一次登录的ip地址")
        @Nonnull
        String lastLoginAddr
) {
}
