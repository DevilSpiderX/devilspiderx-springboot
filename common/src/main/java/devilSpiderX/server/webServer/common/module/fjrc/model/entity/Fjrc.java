package devilSpiderX.server.webServer.common.module.fjrc.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Table(value = "fjrc")
public class Fjrc implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String itemBank;

    private String type;

    private String title;

    private String label;

    private String answer;

    private String a;

    private String b;

    private String c;

    private String d;

    private String e;

    private String f;

    private String g;

    private String topicBasis;

    private String remark;

}

