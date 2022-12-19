package devilSpiderX.server.webServer.core.listener;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

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
