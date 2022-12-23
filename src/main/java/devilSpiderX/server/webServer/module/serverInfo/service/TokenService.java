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
    private final Map<String, List<String>> tokenMap = new HashMap<>();

    public String create(String uid) {
        if (uid == null) {
            return null;
        }
        List<String> tokens = tokenMap.computeIfAbsent(uid, k -> new ArrayList<>());
        String token = _create(uid);
        if (token != null)
            tokens.add(token);
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
        List<String> tokens = tokenMap.get(uid);
        if (tokens == null) {
            return false;
        }
        for (String _token : tokens) {
            if (Objects.equals(token, _token)) {
                return true;
            }
        }
        return false;
    }

    public void destroy(String uid, String token) {
        if (uid == null || token == null) {
            return;
        }
        List<String> tokens = tokenMap.get(uid);
        if (tokens == null) {
            return;
        }
        tokens.remove(token);
    }
}
