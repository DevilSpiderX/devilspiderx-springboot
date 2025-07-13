package devilSpiderX.server.webServer.common.module.query.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import devilSpiderX.server.webServer.common.core.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 密码记录(MyPassword)实体类
 *
 * @author makejava
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "my_password", comment = "密码记录")
public class MyPassword extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column(comment = "记录名")
    private String name;

    @Column(comment = "用户名")
    private String account;

    @Column(comment = "密码")
    private String password;

    @Column(comment = "备注")
    private String remark;

    @Column(comment = "拥有者")
    private Long owner;

    @Column(
            comment = "是否删除",
            isLogicDelete = true
    )
    private Boolean deleted;

}

