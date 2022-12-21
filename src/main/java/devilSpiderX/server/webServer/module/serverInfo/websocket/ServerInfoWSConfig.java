package devilSpiderX.server.webServer.module.serverInfo.websocket;

import devilSpiderX.server.webServer.module.serverInfo.service.TokenService;
import jakarta.annotation.Resource;
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
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class ServerInfoWSConfig implements WebSocketConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoWSConfig.class);
    @Resource(name = "tokenService")
    private TokenService tokenService;
    private final ServerInfoWSHandler serverInfoWSHandler;

    public ServerInfoWSConfig(ServerInfoWSHandler serverInfoWSHandler) {
        this.serverInfoWSHandler = serverInfoWSHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverInfoWSHandler, "/websocket/getServerInfo")
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes
                    ) throws Exception {
                        super.beforeHandshake(request, response, wsHandler, attributes);
                        HttpServletRequest httpReq = ((ServletServerHttpRequest) request).getServletRequest();
                        attributes.put("address", String.format(
                                "%s:%d",
                                httpReq.getRemoteAddr(),
                                httpReq.getRemotePort()
                        ));
                        String uid = (String) attributes.get("uid");
                        if (uid == null) {
                            logger.info("未登录，拒绝连接");
                            return false;
                        }
                        String token = httpReq.getParameter("token");
                        if (tokenService.check(uid, token)) {
                            attributes.put("token", token);
                            return true;
                        }
                        logger.info("用户{}:token验证失败", uid);
                        return false;
                    }
                });
    }
}
