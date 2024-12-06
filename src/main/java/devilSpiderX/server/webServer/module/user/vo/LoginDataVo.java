package devilSpiderX.server.webServer.module.user.vo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LoginDataVo(
        @NotNull
        String uid,
        boolean admin,
        @NotNull
        List<String> roles,
        @NotNull
        List<String> permissions,
        @NotNull
        String lastLoginAddr
) {
}
