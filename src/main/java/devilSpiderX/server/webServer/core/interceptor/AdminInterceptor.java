package devilSpiderX.server.webServer.core.interceptor;

import devilSpiderX.server.webServer.core.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        HttpSession session = req.getSession();
        if (isAdmin(session)) {
            return true;
        }
        req.getRequestDispatcher("/error/notAdmin").forward(req, resp);
        return false;
    }

    public static boolean isAdmin(HttpSession session) {
        User user = Optional.ofNullable((User) session.getAttribute("user")).orElse(new User());
        return user.getAdmin() != null && user.getAdmin();
    }
}
