package devilSpiderX.server.webServer.module.serverInfo.record;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "websocket获取的消息类型")
public record TextMsgData(
        @Schema(description = "命令", examples = {"start", "stop"})
        String cmd,
        @Schema(description = "命令为start时才存在。每次数据发送的间隔时长，单位为毫秒")
        Long cd
) {
    public long cd(long defaultValue) {
        return Objects.requireNonNullElse(cd, defaultValue);
    }
}
