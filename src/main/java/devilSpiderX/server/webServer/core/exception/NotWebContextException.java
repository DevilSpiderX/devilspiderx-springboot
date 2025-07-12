package devilSpiderX.server.webServer.core.exception;


import devilSpiderX.server.webServer.core.resp.ResultCode;

import java.io.Serial;

/**
 * 一个异常：代表当前不是 Web 上下文，无法调用某个 API
 *
 * @author DevilSpiderX
 */
public class NotWebContextException extends BaseException {
    @Serial
    private static final long serialVersionUID = 3868975503362243684L;

    /**
     * 一个异常：代表当前不是 Web 上下文，无法调用某个 API
     *
     * @param message 异常描述
     */
    public NotWebContextException(final String message) {
        super(ResultCode.Undefined, message);
    }

}
