package devilSpiderX.server.webServer.core.exception;

import java.io.Serial;

import static devilSpiderX.server.webServer.core.resp.ResultCode.JacksonUtilError;


/**
 * 一个异常：代表使用Jackson工具类进行序列化反序列化时出现错误
 *
 * @author DevilSpiderX
 */
public class JacksonUtilException extends BaseException {
    @Serial
    private static final long serialVersionUID = -7743913201222447978L;

    public JacksonUtilException() {
        super(JacksonUtilError);
    }

    public JacksonUtilException(final String message) {
        super(JacksonUtilError, message);
    }

    public JacksonUtilException(final String pattern, final Object... args) {
        super(JacksonUtilError, pattern, args);
    }

    public JacksonUtilException(final Throwable cause) {
        super(JacksonUtilError, cause);
    }

    public JacksonUtilException(final Throwable cause, final String message) {
        super(JacksonUtilError, cause, message);
    }

    public JacksonUtilException(final Throwable cause, final String pattern, final Object... args) {
        super(JacksonUtilError, cause, pattern, args);
    }
}
