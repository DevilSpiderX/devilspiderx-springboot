package devilSpiderX.server.webServer.common.core.exception.handler;

import devilSpiderX.server.webServer.common.core.exception.JacksonUtilException;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;

/**
 * @author DevilSpiderX
 */
@RestControllerAdvice
@Order(1)
public class JacksonUtilExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtilExceptionHandler.class);

    @ExceptionHandler({JacksonUtilException.class})
    public CommonResult<Void> handleException(JacksonUtilException jue) {
        final var sb = new StringBuilder(jue.getMessage());
        final var cause = jue.getCause();
        if (cause != null) {
            sb.append(MessageFormat.format(" : cause by {0}", cause.getMessage()));
        }
        final var msg = sb.toString();
        logger.error(msg, jue);
        return CommonResult.of(jue.getCode(), msg);
    }

}
