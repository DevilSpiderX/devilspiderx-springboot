package devilSpiderX.server.webServer.controller;

import devilSpiderX.server.webServer.util.AjaxResp;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/{status:\\d+}")
    public String error_get(@PathVariable String status) {
        return status + ".html";
    }

    @PostMapping("/{status:\\d+}")
    @ResponseBody
    public AjaxResp<?> error_post(@PathVariable int status) {
        return AjaxResp.of(status, HttpStatus.valueOf(status).getReasonPhrase())
                .setData(Map.of(
                        "timestamp", System.currentTimeMillis()
                ));
    }

    @RequestMapping("/notLogin")
    @ResponseBody
    public AjaxResp<?> UserNoLogin() {
        return AjaxResp.notLogin();
    }

    @RequestMapping("/notAdmin")
    @ResponseBody
    public AjaxResp<?> NoAdmin() {
        return AjaxResp.notAdmin();
    }
}
