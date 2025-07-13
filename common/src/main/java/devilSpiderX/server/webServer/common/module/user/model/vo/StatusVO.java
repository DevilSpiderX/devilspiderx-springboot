package devilSpiderX.server.webServer.common.module.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Schema(description = "用户状态VO")
@Data
public class StatusVO implements Serializable {

    @Schema(description = "用户id", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 序列化时转为字符串，防止前端精度丢失
    private long uid = -1;

    @Schema(description = "是否登录")
    private boolean login = false;

    @Schema(description = "是否管理员")
    private boolean admin = false;

    @Schema(description = "用户的角色列表")
    private List<String> roles = Collections.emptyList();

    @Schema(description = "用户权限列表")
    private List<String> permissions = Collections.emptyList();


    public void setRoles(final List<String> roles) {
        if (roles == null) {
            return;
        }
        this.roles = roles;
    }

    public void setPermissions(final List<String> permissions) {
        if (permissions == null) {
            return;
        }
        this.permissions = permissions;
    }
}
