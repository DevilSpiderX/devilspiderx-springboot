package devilSpiderX.server.webServer.module.fjrc.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class FjrcUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 501215794940192284L;

    private String uid;

    private Date time;

    private String value;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

