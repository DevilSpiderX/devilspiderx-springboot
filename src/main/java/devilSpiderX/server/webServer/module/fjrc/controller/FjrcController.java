package devilSpiderX.server.webServer.module.fjrc.controller;

import cn.dev33.satoken.secure.SaSecureUtil;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.fjrc.record.HistoryRequest;
import devilSpiderX.server.webServer.module.fjrc.service.FjrcService;
import org.springframework.web.bind.annotation.*;

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
    public AjaxResp<?> get(@RequestParam(value = "bank", defaultValue = "A") String bank,
                           @RequestParam(value = "id", defaultValue = "0") int id) {
        return AjaxResp.success(fjrcService.getTopic(bank.toUpperCase(Locale.ENGLISH), id));
    }

    @GetMapping("count")
    public AjaxResp<?> count(@RequestParam(value = "bank", defaultValue = "A") String bank) {
        return AjaxResp.success(Map.of("count", fjrcService.getCount(bank.toUpperCase(Locale.ENGLISH))));
    }

    private final Object genFingerprintLock = new Object();

    @GetMapping("onlineCount")
    public AjaxResp<?> onlineCount(@RequestParam(value = "fingerprint", required = false) String fingerprint) {
        if (fingerprint == null) {
            synchronized (genFingerprintLock) {
                final var str = cn.dev33.satoken.util.SaFoxUtil.formatDate(new Date());
                fingerprint = SaSecureUtil.sha256(str);
            }
        }
        return AjaxResp.success(Map.of(
                "count", fjrcService.getOnlineCount(fingerprint),
                "fingerprint", fingerprint
        ));
    }


    @PostMapping("uploadHistory")
    public AjaxResp<?> uploadHistory(@RequestBody HistoryRequest reqBody) {
        final var success = fjrcService.uploadHistory(reqBody.key(), reqBody.value());
        return success ? AjaxResp.success() : AjaxResp.failure();
    }

    @GetMapping("downloadHistory")
    public AjaxResp<?> downloadHistory(@RequestParam String key) {
        final var history = fjrcService.downloadHistory(key);
        if (history == null) {
            return AjaxResp.failure("无历史记录");
        }

        return AjaxResp.success("存在历史记录", history);
    }
}
