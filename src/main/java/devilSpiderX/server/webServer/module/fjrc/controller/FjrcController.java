package devilSpiderX.server.webServer.module.fjrc.controller;

import cn.dev33.satoken.secure.SaSecureUtil;
import devilSpiderX.server.webServer.module.fjrc.entity.Fjrc;
import devilSpiderX.server.webServer.module.fjrc.service.FjrcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.constant.Constable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/fjrc")
public class FjrcController {
    private final FjrcService fjrcService;

    public FjrcController(FjrcService fjrcService) {
        this.fjrcService = fjrcService;
    }

    @GetMapping("topic")
    public Fjrc get(@RequestParam(value = "bank", defaultValue = "A") String bank,
                    @RequestParam(value = "id", defaultValue = "0") int id) {
        return fjrcService.getTopic(bank.toUpperCase(Locale.ENGLISH), id);
    }

    @GetMapping("count")
    public Map<String, Integer> count(@RequestParam(value = "bank", defaultValue = "A") String bank) {
        return Map.of("count", fjrcService.getCount(bank.toUpperCase(Locale.ENGLISH)));
    }

    private final Object genFingerprintLock = new Object();

    @GetMapping("onlineCount")
    public Map<String, Constable> onlineCount(@RequestParam(value = "fingerprint", required = false) String fingerprint) {
        if (fingerprint == null) {
            synchronized (genFingerprintLock) {
                final var str = cn.dev33.satoken.util.SaFoxUtil.formatDate(new Date());
                fingerprint = SaSecureUtil.sha256(str);
            }
        }
        return Map.of(
                "count", fjrcService.getOnlineCount(fingerprint),
                "fingerprint", fingerprint
        );
    }

}
