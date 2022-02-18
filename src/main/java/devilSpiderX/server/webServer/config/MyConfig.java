package devilSpiderX.server.webServer.config;

import com.alibaba.fastjson.JSONArray;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "my")
public class MyConfig {
    private String STATIC_LOCATION;
    private JSONArray ERROR_PAGES;

    public String getSTATIC_LOCATION() {
        return STATIC_LOCATION;
    }

    public void setSTATIC_LOCATION(String STATIC_LOCATION) {
        this.STATIC_LOCATION = STATIC_LOCATION;
    }

    public JSONArray getERROR_PAGES() {
        return ERROR_PAGES;
    }

    public void setERROR_PAGES(List<Map<String, Object>> ERROR_PAGES) {
        this.ERROR_PAGES = new JSONArray();
        this.ERROR_PAGES.addAll(ERROR_PAGES);
    }
}
