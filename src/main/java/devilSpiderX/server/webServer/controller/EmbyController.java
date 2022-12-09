package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/emby")
@CrossOrigin
public class EmbyController {

    @RequestMapping("/admin/service/registration/validateDevice")
    @ResponseBody
    private Map<String, Object> validateDevice() {
        return Map.of(
                "cacheExpirationDays", 365,
                "message", "Device Valid",
                "resultCode", "GOOD"
        );
    }

    @RequestMapping("/admin/service/registration/getStatus")
    @ResponseBody
    private Map<String, Object> getStatus(@RequestBody(required = false) JSONObject reqBody) {
        return Map.of(
                "deviceStatus", "0",
                "planType", "Lifetime",
                "subscriptions", reqBody == null ? Map.of() : reqBody
        );
    }

    @RequestMapping("/admin/service/registration/validate")
    @ResponseBody
    private Map<String, Object> validate() {
        return Map.of(
                "featId", "MBSupporter",
                "registered", true,
                "expDate", "2030-01-01",
                "key", 114514
        );
    }
}
