package devilSpiderX.server.webServer.core.listener;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class MyRequestListener implements ServletRequestListener {
    private final Logger logger = LoggerFactory.getLogger(MyRequestListener.class);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        HttpSession session = req.getSession();
        session.setAttribute("address", req.getRemoteAddr());
        logger.info(String.format("（%s:%d） %s %s %s", req.getRemoteAddr(), req.getRemotePort(), req.getMethod(),
                req.getRequestURI(), req.getProtocol()));
    }
}
