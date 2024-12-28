package devilSpiderX.server.webServer.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "上传用户头像返回Vo")
public record UploadAvatarVo(
        @Schema(description = "用户头像地址")
        @Nonnull String avatar
) {
}
