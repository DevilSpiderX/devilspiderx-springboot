package devilSpiderX.server.webServer.core.controller;

import devilSpiderX.server.webServer.core.exception.NotAdminException;
import devilSpiderX.server.webServer.core.exception.NotLoginException;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/404")
    public ModelAndView error404() {
        return new ModelAndView("forward:/", HttpStatus.OK);
    }

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
    public void UserNoLogin() throws NotLoginException {
        throw new NotLoginException();
    }

    @RequestMapping("/notAdmin")
    public void NoAdmin() throws NotAdminException {
        throw new NotAdminException();
    }
}
