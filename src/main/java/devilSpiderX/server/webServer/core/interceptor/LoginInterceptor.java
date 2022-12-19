package devilSpiderX.server.webServer.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        HttpSession session = req.getSession();
        if (isLogin(session)) {
            return true;
        }
        req.getRequestDispatcher("/error/notLogin").forward(req, resp);
        return false;
    }

    public static boolean isLogin(HttpSession session) {
        return session.getAttribute("user") != null;
    }

}
