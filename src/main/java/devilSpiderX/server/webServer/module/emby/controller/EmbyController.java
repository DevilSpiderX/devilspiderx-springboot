package devilSpiderX.server.webServer.module.emby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/emby")
@CrossOrigin
public class EmbyController {

    @RequestMapping("admin/service/registration/validateDevice")
    @ResponseBody
    private Map<String, Object> validateDevice() {
        return Map.of(
                "cacheExpirationDays", 365,
                "message", "Device Valid",
                "resultCode", "GOOD"
        );
    }

    @RequestMapping("admin/service/registration/getStatus")
    @ResponseBody
    private Map<String, Object> getStatus(@RequestParam Map<String, String> reqBody) {
        return Map.of(
                "deviceStatus", "0",
                "planType", "Lifetime",
                "subscriptions", reqBody
        );
    }

    @RequestMapping("admin/service/registration/validate")
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
