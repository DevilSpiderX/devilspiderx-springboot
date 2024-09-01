package devilSpiderX.server.webServer.module.user.vo;

import org.jetbrains.annotations.NotNull;

public record LoginVo(
        int status,
        @NotNull
        String reason,
        LoginDataVo data
) {
}
