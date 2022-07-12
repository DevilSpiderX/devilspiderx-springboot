package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.controller.response.ResultMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

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
    public ResultMap<Void> UserNoLogin() {
        ResultMap<Void> resultMap = new ResultMap<>();
        resultMap.setCode(100);
        resultMap.setMsg("没有权限，请登录");
        return resultMap;
    }

    @RequestMapping("/noAdmin")
    @ResponseBody
    public ResultMap<Void> NoAdmin() {
        ResultMap<Void> resultMap = new ResultMap<>();
        resultMap.setCode(101);
        resultMap.setMsg("没有管理员权限");
        return resultMap;
    }
}
