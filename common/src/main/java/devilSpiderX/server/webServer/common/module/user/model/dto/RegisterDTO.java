package devilSpiderX.server.webServer.common.module.user.model.dto;

import devilSpiderX.server.webServer.common.core.constant.DigestConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;


@Schema(description = "注册请求参数")
public record RegisterDTO(

        @Schema(description = "用户名")
        @NotBlank(message = "用户名不能为空")
        String username,

        @Schema(description = "密码")
        @NotBlank(message = "密码不能为空")
        @Size(min = DigestConstant.minPasswordLength, message = "密码长度不能少于8位")
        String password

) implements Serializable {
}
