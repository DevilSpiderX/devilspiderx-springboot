package devilSpiderX.server.webServer.module.serverInfo.service;

import devilSpiderX.server.webServer.core.util.MyCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("tokenService")
public class TokenService {
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    record Token(String value, long createdTime) {
        boolean isAlive() {
            return System.currentTimeMillis() - createdTime <= 10 * 60 * 1000;
        }
    }

    private final Map<String, List<Token>> tokenMap = new HashMap<>();


    public String create(String uid) {
        if (uid == null) {
            return null;
        }
        List<Token> tokens = tokenMap.computeIfAbsent(uid, k -> new ArrayList<>());

        List<Token> expired = new ArrayList<>();
        for (Token token : tokens) {
            if (!token.isAlive()) {
                expired.add(token);
            }
        }
        tokens.removeAll(expired);

        String token = _create(uid);
        if (token != null)
            tokens.add(new Token(token, System.currentTimeMillis()));
        return token;
    }

    private String _create(String uid) {
        String timeStr = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
        );
        try {
            byte[] digest = MyCipher.MD5(uid + timeStr);
            return MyCipher.bytes2Hex(digest);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean check(String uid, String token) {
        if (uid == null || token == null) {
            return false;
        }
        List<Token> tokens = tokenMap.get(uid);
        if (tokens == null) {
            return false;
        }
        for (Token _token : tokens) {
            if (_token.isAlive() && Objects.equals(token, _token.value())) {
                return true;
            }
        }
        return false;
    }

    public void destroy(String uid, String token) {
        if (uid == null || token == null) {
            return;
        }
        List<Token> tokens = tokenMap.get(uid);
        if (tokens == null) {
            return;
        }
        List<Token> willRemove = new ArrayList<>();
        for (Token _token : tokens) {
            if (!_token.isAlive() || Objects.equals(_token.value, token)) {
                willRemove.add(_token);
            }
        }
        tokens.removeAll(willRemove);
    }
}
