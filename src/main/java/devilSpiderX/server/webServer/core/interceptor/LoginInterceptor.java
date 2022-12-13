package devilSpiderX.server.webServer.core.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
