package devilSpiderX.server.webServer.module.emby.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/emby")
@CrossOrigin
public class EmbyController {

    @RequestMapping("admin/service/registration/validateDevice")
    public Map<String, Object> validateDevice() {
        return Map.of(
                "cacheExpirationDays", 365,
                "message", "Device Valid",
                "resultCode", "GOOD"
        );
    }

    @RequestMapping("admin/service/registration/getStatus")
    public Map<String, Object> getStatus(@RequestParam Map<String, String> reqBody) {
        return Map.of(
                "deviceStatus", "0",
                "planType", "Lifetime",
                "subscriptions", reqBody
        );
    }

    @RequestMapping("admin/service/registration/validate")
    public Map<String, Object> validate() {
        return Map.of(
                "featId", "MBSupporter",
                "registered", true,
                "expDate", "2030-01-01",
                "key", 114514
        );
    }
}
