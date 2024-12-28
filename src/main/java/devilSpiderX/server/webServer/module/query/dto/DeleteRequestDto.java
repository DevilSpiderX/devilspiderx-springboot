package devilSpiderX.server.webServer.module.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@Schema(description = "删除密码记录请求参数")
public record DeleteRequestDto(
        @Schema(description = "记录id", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        Integer id
) {
    public DeleteRequestDto {
        Objects.requireNonNull(id, "id参数不能为空或不存在");
    }
}
