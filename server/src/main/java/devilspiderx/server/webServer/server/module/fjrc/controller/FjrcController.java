package devilSpiderX.server.webServer.server.module.fjrc.controller;

import cn.dev33.satoken.util.SaFoxUtil;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import devilSpiderX.server.webServer.common.core.util.DigestUtils;
import devilSpiderX.server.webServer.common.module.fjrc.model.dto.HistoryRequest;
import devilSpiderX.server.webServer.common.module.fjrc.model.entity.Fjrc;
import devilSpiderX.server.webServer.common.module.fjrc.model.vo.HistoryVO;
import devilSpiderX.server.webServer.common.module.fjrc.model.vo.ItemCountVO;
import devilSpiderX.server.webServer.common.module.fjrc.model.vo.OnlineCountVO;
import devilSpiderX.server.webServer.server.module.fjrc.service.FjrcService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.Locale;

@Hidden
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fjrc")
public class FjrcController {

    private final FjrcService fjrcService;


    @GetMapping("topic")
    public ResponseEntity<CommonResult<Fjrc>> get(
            final
            @RequestParam(value = "bank", defaultValue = "A")
            String bank,

            final
            @RequestParam(value = "id", defaultValue = "0")
            int id
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                .body(CommonResult.success(fjrcService.getTopic(bank.toUpperCase(Locale.ENGLISH), id)));
    }

    @GetMapping("count")
    public ResponseEntity<CommonResult<ItemCountVO>> count(
            final
            @RequestParam(value = "bank", defaultValue = "A")
            String bank
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                .body(CommonResult.success(new ItemCountVO(
                        fjrcService.getCount(bank.toUpperCase(Locale.ENGLISH))
                )));
    }

    private final Object genFingerprintLock = new Object();

    @GetMapping("onlineCount")
    public CommonResult<OnlineCountVO> onlineCount(
            final
            @RequestParam(value = "fingerprint", required = false)
            String fingerprint
    ) {
        final String _fingerprint;
        if (fingerprint == null) {
            synchronized (genFingerprintLock) {
                final var str = SaFoxUtil.formatDate(new Date());
                _fingerprint = DigestUtils.sha256(str);
            }
        } else {
            _fingerprint = fingerprint;
        }
        return CommonResult.success(new OnlineCountVO(
                fjrcService.getOnlineCount(_fingerprint),
                _fingerprint
        ));
    }


    @PostMapping("uploadHistory")
    public CommonResult<Boolean> uploadHistory(
            final
            @RequestBody
            HistoryRequest reqBody
    ) {
        final var success = fjrcService.uploadHistory(reqBody.key(), reqBody.value());
        return CommonResult.success(success);
    }

    @GetMapping("downloadHistory")
    public CommonResult<HistoryVO> downloadHistory(
            final
            @RequestParam
            String key
    ) {
        final var history = fjrcService.downloadHistory(key);
        if (history == null) {
            return CommonResult.error("无历史记录");
        }

        return CommonResult.success("存在历史记录", history);
    }
}
