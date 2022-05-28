package devilSpiderX.server.webServer.controller;

import devilSpiderX.server.webServer.filter.UserFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class WelcomeController {
    @GetMapping("/")
    public void welcome(HttpSession session, HttpServletResponse resp) throws IOException {
        if (UserFilter.isOperable(session)) {
            resp.sendRedirect("/index.html");
        } else {
            resp.sendRedirect("/login.html");
        }
    }
}
