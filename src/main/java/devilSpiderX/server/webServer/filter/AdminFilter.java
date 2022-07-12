package devilSpiderX.server.webServer.filter;

import devilSpiderX.server.webServer.service.UserService;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@WebFilter(filterName = "AdminFilter", urlPatterns = {"/api/os/*", "/api/service/shutdown", "/api/v2ray/*"})
public class AdminFilter implements Filter {

    @Resource(name = "userService")
    private UserService userService;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        String uid = Objects.toString(session.getAttribute("uid"), null);
        if (userService.isAdmin(uid)) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/error/noAdmin").forward(request, response);
        }
    }
}
