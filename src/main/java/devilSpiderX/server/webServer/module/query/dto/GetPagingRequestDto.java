package devilSpiderX.server.webServer.module.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

@Schema(description = "分页查询密码记录请求参数")
public record GetPagingRequestDto(
        @Schema(description = "查询值", nullable = true)
        @Nullable
        String key,
        @Schema(description = "每页的长度", nullable = true)
        @Nonnull
        Integer length,
        @Schema(description = "查询第n页", nullable = true)
        @Nonnull
        Integer page
) {

    public GetPagingRequestDto(@Nullable String key, @Nullable Integer length, @Nullable Integer page) {
        this.key = key;
        this.length = Objects.requireNonNullElse(length, 20);
        this.page = Objects.requireNonNullElse(page, 0);
    }

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
