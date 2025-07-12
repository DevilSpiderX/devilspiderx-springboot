package devilSpiderX.server.webServer.core.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author DevilSpiderX
 */
@Data
@Schema(description = "通用返回类型")
public class CommonResult<T> implements Serializable {

    @Schema(description = "业务代码")
    private final int code;
    @Schema(description = "消息")
    private final @Nonnull String msg;
    @Schema(description = "数据", nullable = true)
    private T data;

    public CommonResult(final int code, final String msg, final T data) {
        this.code = code;
        this.msg = Objects.requireNonNullElse(
                msg,
                ResultCode.fromCode(code)
                        .getMessage()
        );
        this.data = data;
    }

    public static <T> @Nonnull CommonResult<T> success() {
        return of(ResultCode.Success);
    }

    public static <T> @Nonnull CommonResult<T> success(T data) {
        return ofData(ResultCode.Success, data);
    }

    public static <T> @Nonnull CommonResult<T> success(String msg, T data) {
        return of(ResultCode.Success, msg, data);
    }

    public static <T> @Nonnull CommonResult<T> successMsg(String msg) {
        return of(ResultCode.Success, msg);
    }

    public static <T> @Nonnull CommonResult<T> error() {
        return of(ResultCode.Error);
    }

    public static <T> @Nonnull CommonResult<T> error(String msg) {
        return of(ResultCode.Error, msg);
    }


    // <-- 通用生成方法 -->

    public static <T> @Nonnull CommonResult<T> of(final int code) {
        return new CommonResult<>(code, null, null);
    }

    public static <T> @Nonnull CommonResult<T> of(int code, final String msg) {
        return new CommonResult<>(code, msg, null);
    }

    public static <T> @Nonnull CommonResult<T> of(int code, final String msg, final T data) {
        return new CommonResult<>(code, msg, data);
    }

    public static <T> @Nonnull CommonResult<T> of(final ResultCode resultCode) {
        return of(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> @Nonnull CommonResult<T> of(final ResultCode resultCode, final String msg) {
        return of(resultCode.getCode(), msg);
    }

    public static <T> @Nonnull CommonResult<T> ofData(final ResultCode resultCode, final T data) {
        return of(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> @Nonnull CommonResult<T> of(final ResultCode resultCode, final String msg, final T data) {
        return of(resultCode.getCode(), msg, data);
    }
}
