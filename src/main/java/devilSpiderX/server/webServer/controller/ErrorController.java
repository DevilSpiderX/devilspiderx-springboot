package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/{status:\\d+}")
    public String error_get(@PathVariable String status) {
        return status + ".html";
    }

    @PostMapping("/{status:\\d+}")
    @ResponseBody
    public JSONObject error_post(@PathVariable int status) {
        JSONObject respJson = new JSONObject();
        respJson.put("timestamp", System.currentTimeMillis());
        respJson.put("status", status);
        respJson.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        return respJson;
    }

    @RequestMapping("/userNoLogin")
    @ResponseBody
    public JSONObject UserNoLogin() {
        JSONObject respJson = new JSONObject();
        respJson.put("code", "100");
        respJson.put("msg", "没有权限，请登录");
        return respJson;
    }
}
