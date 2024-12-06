package devilSpiderX.server.webServer.module.serverInfo.vo;

import org.jetbrains.annotations.NotNull;

public record NetworkVo(
        @NotNull
        String name,
        @NotNull
        String displayName,
        @NotNull
        String macAddr,
        long bytesSent,
        long bytesRecv,
        long uploadSpeed,
        long downloadSpeed,
        @NotNull
        String[] IPv4addr,
        @NotNull
        String[] IPv6addr
) {
}
