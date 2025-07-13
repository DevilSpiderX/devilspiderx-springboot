package devilSpiderX.server.webServer.common.module.fjrc.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Table(value = "fjrc_user")
public class FjrcUser implements Serializable {

    @Id(keyType = KeyType.None)
    private String uid;

    private Date time;

    private String value;

}

