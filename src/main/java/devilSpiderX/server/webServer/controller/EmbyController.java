package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/emby")
@CrossOrigin
public class EmbyController {

    @RequestMapping("/admin/service/registration/validateDevice")
    @ResponseBody
    private JSONObject validateDevice() {
        JSONObject respJson = new JSONObject();
        respJson.put("cacheExpirationDays", 365);
        respJson.put("message", "Device Valid");
        respJson.put("resultCode", "GOOD");
        return respJson;
    }

    @RequestMapping("/admin/service/registration/getStatus")
    @ResponseBody
    private JSONObject getStatus() {
        JSONObject respJson = new JSONObject();
        respJson.put("deviceStatus", "0");
        respJson.put("planType", "Lifetime");
        respJson.put("subscriptions", new JSONObject());
        return respJson;
    }

    @RequestMapping("/admin/service/registration/validate")
    @ResponseBody
    private JSONObject validate() {
        JSONObject respJson = new JSONObject();
        respJson.put("featId", "MBSupporter");
        respJson.put("registered", true);
        respJson.put("expDate", "2030-01-01");
        respJson.put("key", 114514);
        return respJson;
    }
}
