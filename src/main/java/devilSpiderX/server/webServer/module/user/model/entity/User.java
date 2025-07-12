package devilSpiderX.server.webServer.module.user.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户(User)实体类
 *
 * @author makejava
 */
@Data
@Table(value = "user", comment = "用户")
public class User implements Serializable {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    @Column(comment = "账号")
    private String username;

    @Column(comment = "密码")
    private String password;

    @Column(comment = "是否管理员")
    private Boolean admin;

    @Column(comment = "上次登录ip地址")
    private String lastAddress;

    @Column(comment = "头像地址")
    private String avatar;

}

