package devilSpiderX.server.webServer.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "UserFilter",
        urlPatterns = {"/command", "/query", "/v2ray", "/addPasswords", "/updatePasswords", "/service/shutdown"})
public class UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        if (isOperable(session)) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/error/userNoLogin").forward(request, response);
        }
    }

    public static boolean isOperable(HttpSession session) {
        return session.getAttribute("operable") != null && (Boolean) session.getAttribute("operable");
    }
}
