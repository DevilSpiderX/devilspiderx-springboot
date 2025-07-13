package devilSpiderX.server.webServer.common.module.query.model.vo;

import com.mybatisflex.annotation.TableRef;
import devilSpiderX.server.webServer.common.module.query.model.entity.MyPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "密码记录VO")
@Data
@TableRef(MyPassword.class)
public class MyPasswordsVO implements Serializable {

    @Schema(description = "记录id")
    private int id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "备注")
    private String remark;

}