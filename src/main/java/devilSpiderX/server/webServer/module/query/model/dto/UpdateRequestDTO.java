package devilSpiderX.server.webServer.module.query.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@Schema(description = "修改密码记录请求参数")
public record UpdateRequestDTO(

        @Schema(description = "名称")
        String name,

        @Schema(description = "账号")
        String account,

        @Schema(description = "密码")
        String password,

        @Schema(description = "备注")
        String remark

) implements Serializable {
}
