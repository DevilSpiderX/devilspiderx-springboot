package devilSpiderX.server.webServer.server.module.emby.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用于Emby Premiere验证")
@RestController
@RequestMapping("/emby")
@CrossOrigin
public class EmbyController {

    @Operation(summary = "设备验证")
    @GetMapping("admin/service/registration/validateDevice")
    public Map<String, Object> validateDevice() {
        return Map.of(
                "cacheExpirationDays", 365,
                "message", "Device Valid",
                "resultCode", "GOOD"
        );
    }

    @Operation(summary = "获取设备状态")
    @GetMapping("admin/service/registration/getStatus")
    public Map<String, Object> getStatus(@RequestParam Map<String, String> reqBody) {
        return Map.of(
                "deviceStatus", "0",
                "planType", "Lifetime",
                "subscriptions", reqBody
        );
    }

    @Operation(summary = "Key验证")
    @GetMapping("admin/service/registration/validate")
    public Map<String, Object> validate() {
        return Map.of(
                "featId", "MBSupporter",
                "registered", true,
                "expDate", "2030-01-01",
                "key", 114514
        );
    }
}
