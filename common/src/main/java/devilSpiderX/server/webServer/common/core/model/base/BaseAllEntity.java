package devilSpiderX.server.webServer.common.core.model.base;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 其他Entity继承该类，可自动填写create_time、update_time、create_user、update_user字段
 *
 * @author DevilSpiderX
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseAllEntity extends BaseEntity {
    @Column(comment = "创建人id")
    private Long createUser;

    @Column(comment = "更新人id")
    private Long updateUser;
}
