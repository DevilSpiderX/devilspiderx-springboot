package devilSpiderX.server.webServer.module.serverInfo.vo;

import org.jetbrains.annotations.NotNull;

public record CurrentOSVo(
        @NotNull
        String name,
        int bitness,
        int processCount
) {
}
