package devilSpiderX.server.webServer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebListener
public class MyRequestListener implements ServletRequestListener {
    private final Logger logger = LoggerFactory.getLogger(MyRequestListener.class);
    private long counter = 0;

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        HttpSession session = req.getSession();
        session.setAttribute("address", req.getRemoteAddr());
        logger.info(String.format("%d.（%s） %s %s %s", counter++, req.getRemoteAddr(), req.getMethod(),
                req.getRequestURI(), req.getProtocol()));
    }
}
