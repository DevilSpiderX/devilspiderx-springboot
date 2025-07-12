package devilSpiderX.server.webServer.core.exception;

import devilSpiderX.server.webServer.core.resp.ResultCode;
import jakarta.annotation.Nonnull;
import lombok.Getter;

import java.io.Serial;

import static devilSpiderX.server.webServer.core.util.SimpleStringFormatter.format;


/**
 * 业务基础异常类型
 *
 * @author DevilSpiderX
 */
@Getter
public class BaseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 679814604476244205L;

    /**
     * 业务异常码
     */
    private final int code;

    private ResultCode resultCode;

    public BaseException(int code) {
        this.code = code;
    }

    public BaseException(int code, final String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, final String pattern, final Object... args) {
        super(format(pattern, args));
        this.code = code;
    }

    public BaseException(final int code, final Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(final int code, final Throwable cause, final String message) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(final int code, final Throwable cause, final String pattern, final Object... args) {
        super(format(pattern, args), cause);
        this.code = code;
    }

    public BaseException(final @Nonnull ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BaseException(final @Nonnull ResultCode resultCode, final String message) {
        this(resultCode.getCode(), message);
        this.resultCode = resultCode;
    }

    public BaseException(final @Nonnull ResultCode resultCode, final String pattern, final Object... args) {
        this(resultCode.getCode(), pattern, args);
        this.resultCode = resultCode;
    }

    public BaseException(final @Nonnull ResultCode resultCode, final Throwable cause) {
        this(resultCode.getCode(), cause);
        this.resultCode = resultCode;
    }

    public BaseException(final @Nonnull ResultCode resultCode, final Throwable cause, final String message) {
        this(resultCode.getCode(), cause, message);
        this.resultCode = resultCode;
    }

    public BaseException(
            final @Nonnull ResultCode resultCode,
            final Throwable cause,
            final String pattern,
            final Object... args
    ) {
        this(resultCode.getCode(), cause, pattern, args);
        this.resultCode = resultCode;
    }

    public @Nonnull ResultCode getResultCode() {
        if (resultCode == null) {
            resultCode = ResultCode.fromCode(code);
        }
        return resultCode;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     */
    @Override
    public @Nonnull String getMessage() {
        final var msg = super.getMessage();
        if (msg != null) {
            return msg;
        }

        return getResultCode().getMessage();
    }

}
