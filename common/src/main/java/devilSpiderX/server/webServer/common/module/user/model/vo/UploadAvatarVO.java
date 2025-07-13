package devilSpiderX.server.webServer.common.module.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "上传用户头像返回VO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadAvatarVO implements Serializable {

    @Schema(description = "用户头像地址")
    private String avatar;

}
