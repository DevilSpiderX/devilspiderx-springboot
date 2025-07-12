package devilSpiderX.server.webServer.core.model.setting.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@Table(value = "setting", comment = "系统配置")
public class Setting implements Serializable {
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String key;

    private String value;

    public Setting(String key) {
        this.key = key;
    }
}

