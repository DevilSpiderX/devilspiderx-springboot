package devilSpiderX.server.webServer.module.serverInfo.websocket;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoWSConfig.class);

    private final ServerInfoWSHandler serverInfoWSHandler;

    public ServerInfoWSConfig(ServerInfoWSHandler serverInfoWSHandler) {
        this.serverInfoWSHandler = serverInfoWSHandler;
    }

    @Override
    public void registerWebSocketHandlers(@Nonnull WebSocketHandlerRegistry registry) {
        registry.addHandler(serverInfoWSHandler, "/websocket/getServerInfo")
                .addInterceptors(new ServerInfoWSInterceptor());
    }

    private static class ServerInfoWSInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(
                @Nonnull ServerHttpRequest request,
                @Nonnull ServerHttpResponse response,
                @Nonnull WebSocketHandler wsHandler,
                @Nonnull Map<String, Object> attributes
        ) {
            if (request instanceof ServletServerHttpRequest _request) {
                final HttpServletRequest httpReq = _request.getServletRequest();

                final String token = httpReq.getParameter("token");
                StpUtil.setTokenValue(token);

                if (!StpUtil.isLogin()) {
                    logger.info("未登录，拒绝连接");
                    return false;
                }

                attributes.put("user", StpUtil.getSession().get("user"));
                attributes.put("token", token);

                return true;
            }
            return false;
        }

        @Override
        public void afterHandshake(
                @Nonnull ServerHttpRequest request,
                @Nonnull ServerHttpResponse response,
                @Nonnull WebSocketHandler wsHandler,
                @Nullable Exception exception) {
        }
    }
}
