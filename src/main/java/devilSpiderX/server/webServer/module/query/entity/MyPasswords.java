package devilSpiderX.server.webServer.module.query.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.annotation.Nonnull;

import java.io.Serial;
import java.io.Serializable;

public class MyPasswords implements Serializable, Comparable<MyPasswords> {
    @Serial
    private static final long serialVersionUID = 1594978966205L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String name;
    private String account;
    private String password;//只储存加密后的密码，禁止明文保存
    private String remark;
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "MyPasswords{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", remark='" + remark + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }

    @Override
    public int compareTo(@Nonnull MyPasswords another) {
        return id.compareTo(another.id);
    }
}