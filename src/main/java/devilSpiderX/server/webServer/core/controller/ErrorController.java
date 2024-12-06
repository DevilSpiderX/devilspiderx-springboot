package devilSpiderX.server.webServer.core.controller;

import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.core.vo.ErrorPostVo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("404")
    public ModelAndView error404() {
        return new ModelAndView("forward:/", HttpStatus.OK);
    }

    @RequestMapping("{status:\\d+}")
    @ResponseBody
    public AjaxResp<ErrorPostVo> error_post(@PathVariable int status) {
        return AjaxResp.<ErrorPostVo>of(status, HttpStatus.valueOf(status).getReasonPhrase())
                .setData(new ErrorPostVo(System.currentTimeMillis()));
    }
}
