package devilSpiderX.server.webServer.listener;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpSessionRegister implements HttpSessionListener {
    private static final Map<String, HttpSession> map = new HashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        map.put(session.getId(), session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        map.remove(session.getId(), session);
    }

    public static HttpSession getHttpSession(String sessionId) {
        return map.get(sessionId);
    }
}
