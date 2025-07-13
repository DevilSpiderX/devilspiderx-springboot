package devilSpiderX.server.webServer.common.module.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "登录状态VO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginVO implements Serializable {

    @Schema(description = "用户id", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 序列化时转为字符串，防止前端精度丢失
    private long uid;

    @Schema(description = "token")
    private String token;

    @Schema(description = "上一次登录的ip地址")
    private String lastLoginAddr;

}
