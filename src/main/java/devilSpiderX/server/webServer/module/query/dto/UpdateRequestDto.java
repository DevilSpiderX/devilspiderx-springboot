package devilSpiderX.server.webServer.module.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

import java.util.Objects;


@Schema(description = "修改密码记录请求参数")
public record UpdateRequestDto(
        @Schema(description = "记录id", requiredMode = Schema.RequiredMode.REQUIRED)
        @Nonnull
        Integer id,
        @Schema(description = "名称")
        String name,
        @Schema(description = "账号")
        String account,
        @Schema(description = "密码")
        String password,
        @Schema(description = "备注")
        String remark
) {
    public UpdateRequestDto {
        Objects.requireNonNull(id, "id参数不能为空或不存在");
    }
}
