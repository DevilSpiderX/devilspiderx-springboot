package devilSpiderX.server.webServer.module.serverInfo.vo;

import org.jetbrains.annotations.NotNull;

public record DiskVo(
        @NotNull
        String label,
        @NotNull
        String mount,
        @NotNull
        String fSType,
        @NotNull
        String name,
        long total,
        long free,
        long used
) {
}
