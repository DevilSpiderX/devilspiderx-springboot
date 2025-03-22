package devilSpiderX.server.webServer.module.serverInfo.controller;

import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.JacksonUtil;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "系统软硬件信息SSE接口")
@RestController
@RequestMapping("/api/ServerInfo")
public class ServerInfoSSEController {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoSSEController.class);
    public static final String notLoginEvent = "event:notLogin\ndata:[DONE]\n\n";

    private final ServerInfoService serverInfoService;

    public ServerInfoSSEController(final ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    @GetMapping("sse")
    public void sendServerInfo(
            @RequestParam(defaultValue = "1000") final long cd,
            final HttpServletResponse response
    ) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        try (final var writer = response.getWriter()) {
            final var currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                if (!StpUtil.isLogin()) {
                    writer.print(notLoginEvent);
                    writer.flush();
                    break;
                }

                final var data = serverInfoService.getServerInfo();
                final var event = "event:message\ndata:%s\n\n".formatted(
                        JacksonUtil.toJSONString(data)
                                .replaceAll("\n", "\ndata:")
                );
                writer.print(event);
                writer.flush();
                Thread.sleep(cd);
            }
        } catch (IOException | InterruptedException ignored) {
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
