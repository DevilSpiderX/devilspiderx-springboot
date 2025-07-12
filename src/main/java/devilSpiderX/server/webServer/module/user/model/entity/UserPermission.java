package devilSpiderX.server.webServer.module.user.model.entity;

import java.io.Serializable;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 用户权限(UserPermission)实体类
 *
 * @author makejava
 */
@Data
@Table(value = "user_permission", comment = "用户权限")
public class UserPermission implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;
    
    @Column(comment = "用户id")
    private Long uid;
    
    @Column(comment = "权限")
    private String permission;
    
}

