package devilSpiderX.server.webServer.common.module.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Schema(description = "修改密码请求参数")
public record UpdatePasswordDTO(

        @Schema(description = "旧密码")
        @NotBlank(message = "旧密码不能为空")
        String oldPassword,

        @Schema(description = "新密码")
        @NotBlank(message = "新密码不能为空")
        String newPassword

) implements Serializable {
}
