package devilSpiderX.server.webServer.module.user.vo;

import org.jetbrains.annotations.NotNull;

public record RegisterVo(
        int status,
        @NotNull
        String reason
) {
}
