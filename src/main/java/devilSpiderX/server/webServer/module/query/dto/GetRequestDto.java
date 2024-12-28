package devilSpiderX.server.webServer.module.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema(description = "查询密码记录请求参数")
public record GetRequestDto(
        @Schema(description = "查询值", nullable = true)
        @Nullable
        String key
) {
    /**
     * 分割查询值,使用空格和<code>.</code>来分割
     *
     * @return 分割后的查询值
     */
    public String[] keys() {
        if (key != null) {
            return key.trim().split("(\\s|\\.)+");
        }
        return new String[0];
    }
}
