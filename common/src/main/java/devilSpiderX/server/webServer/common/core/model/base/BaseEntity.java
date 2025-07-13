package devilSpiderX.server.webServer.common.core.model.base;

import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 其他Entity继承该类，可自动填写create_time、update_time字段
 *
 * @author DevilSpiderX
 */
@Data
public class BaseEntity implements Serializable {
    @Column(comment = "创建时间")
    private LocalDateTime createTime;

    @Column(comment = "更新时间")
    private LocalDateTime updateTime;
}
