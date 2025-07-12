package devilSpiderX.server.webServer.module.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Schema(description = "登录请求参数")
public record LoginDTO(

        @Schema(description = "用户名")
        @NotBlank(message = "用户名不能为空")
        String username,

        @Schema(description = "密码")
        @NotBlank(message = "密码不能为空")
        String password

) implements Serializable {
}
