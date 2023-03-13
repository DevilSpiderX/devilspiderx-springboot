package devilSpiderX.server.webServer.module.serverInfo.websocket;

import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.module.serverInfo.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class ServerInfoWSConfig implements WebSocketConfigurer {
    private final Logger logger = LoggerFactory.getLogger(ServerInfoWSConfig.class);
    private final TokenService tokenService;
    private final ServerInfoWSHandler serverInfoWSHandler;

    public ServerInfoWSConfig(TokenService tokenService, ServerInfoWSHandler serverInfoWSHandler) {
        this.tokenService = tokenService;
        this.serverInfoWSHandler = serverInfoWSHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverInfoWSHandler, "/websocket/getServerInfo")
                .addInterceptors(new ServerInfoWSInterceptor());
    }

    private class ServerInfoWSInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes
        ) {
            if (!StpUtil.isLogin()) {
                logger.info("未登录，拒绝连接");
                return false;
            }

            final String uid = StpUtil.getLoginIdAsString();
            attributes.put("uid", uid);
            attributes.put("user", StpUtil.getSession().get("user"));
            if (request instanceof ServletServerHttpRequest _request) {
                HttpServletRequest httpReq = _request.getServletRequest();
                attributes.put("address", "%s:%d".formatted(
                        httpReq.getRemoteAddr(),
                        httpReq.getRemotePort()
                ));

                final String token = httpReq.getParameter("token");
                if (tokenService.check(uid, token)) {
                    attributes.put("token", token);
                    return true;
                }
                logger.info("用户{}:token验证失败", uid);
            }
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        }
    }
}
