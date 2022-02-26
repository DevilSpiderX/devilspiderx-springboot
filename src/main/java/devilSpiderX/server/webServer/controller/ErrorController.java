package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController {

    @GetMapping("/error/{status:\\d+}")
    public String get(@PathVariable String status) {
        return status + ".html";
    }

    @PostMapping("/error/{status:\\d+}")
    @ResponseBody
    public JSONObject post(@PathVariable int status) {
        JSONObject respJson = new JSONObject();
        respJson.put("timestamp", System.currentTimeMillis());
        respJson.put("status", status);
        respJson.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        return respJson;
    }
}
