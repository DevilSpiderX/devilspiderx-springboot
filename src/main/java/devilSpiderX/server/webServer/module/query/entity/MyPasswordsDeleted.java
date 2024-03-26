package devilSpiderX.server.webServer.module.query.entity;

import java.io.Serial;
import java.io.Serializable;

public class MyPasswordsDeleted implements Serializable {
    @Serial
    private static final long serialVersionUID = -41168418438567306L;

    private Integer id;
    private String name;
    private String account;
    private String password;
    private String remark;
    private String owner;

    public MyPasswordsDeleted(MyPasswords entity) {
        id = entity.getId();
        name = entity.getName();
        account = entity.getAccount();
        password = entity.getPassword();
        remark = entity.getRemark();
        owner = entity.getOwner();
    }

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
}

