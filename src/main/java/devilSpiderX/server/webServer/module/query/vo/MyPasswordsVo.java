package devilSpiderX.server.webServer.module.query.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "密码记录Vo")
public record MyPasswordsVo(
        @Schema(description = "记录id")
        int id,
        @Schema(description = "名称")
        String name,
        @Schema(description = "账号")
        String account,
        @Schema(description = "密码")
        String password,
        @Schema(description = "备注")
        String remark
) {
}