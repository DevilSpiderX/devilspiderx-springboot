package devilSpiderX.server.webServer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class WelcomeController {
    @GetMapping("/")
    public String welcome(HttpSession session) {
//        if (UserFilter.isOperable(session)) {
//            return "redirect:/index.html";
//        } else {
//            return "redirect:/login.html";
//        }
        return "/welcome.html";
    }
}
