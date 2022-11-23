package devilSpiderX.server.webServer.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        if (isLogged(session)) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/error/userNoLogin").forward(request, response);
        }
    }

    public static boolean isLogged(HttpSession session) {
        return session.getAttribute("logged") != null && (Boolean) session.getAttribute("logged");
    }
}
