package devilSpiderX.server.webServer.module.query.entity;

import java.io.Serial;
import java.io.Serializable;

public class MyPasswords implements Serializable, Comparable<MyPasswords> {
    @Serial
    private static final long serialVersionUID = 1594978966205L;

    private Integer id;
    private String name;
    private String account;
    private String password;//只储存加密后的密码，禁止明文保存
    private String remark;
    private String owner;

    private Boolean deleted;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public int compareTo(MyPasswords another) {
        return id.compareTo(another.id);
    }
}