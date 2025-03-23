package devilSpiderX.server.webServer.module.serverInfo.controller;

import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.JacksonUtil;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@Tag(name = "系统软硬件信息SSE接口")
@RestController
@RequestMapping("/api/ServerInfo")
public class ServerInfoSSEController {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoSSEController.class);
    private static final String notLoginEvent = "event:notLogin\ndata:[DONE]\n\n";
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicLong counter = new AtomicLong();

        @Override
        public Thread newThread(@Nonnull final Runnable runnable) {
            final var num = counter.getAndIncrement();
            return Thread.ofVirtual()
                    .name("sse-emitter-%d".formatted(num))
                    .unstarted(runnable);
        }
    };

    private final ServerInfoService serverInfoService;

    public ServerInfoSSEController(final ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    @Operation(summary = "系统信息SSE")
    @GetMapping("sse")
    public void sendServerInfo(
            @Parameter(description = "推送时间间隔")
            @RequestParam(defaultValue = "1000") final long cd,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) {
        final var asyncContext = request.startAsync();
        asyncContext.setTimeout(0);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        final var token = StpUtil.getTokenValue();

        threadFactory.newThread(() -> {
                    final var currentThread = Thread.currentThread();

                    try (final var writer = response.getWriter()) {
                        while (!currentThread.isInterrupted()) {
                            if (Objects.isNull(StpUtil.getLoginIdByToken(token))) {
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
                    } catch (IOException ignored) {
                    } catch (InterruptedException e) {
                        currentThread.interrupt();
                    } catch (RuntimeException e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        asyncContext.complete();
                    }
                })
                .start();
    }
}
