package devilSpiderX.server.webServer.core.jackson.exception.handler;

import devilSpiderX.server.webServer.core.util.AjaxCode;
import devilSpiderX.server.webServer.core.jackson.exception.JacksonUtilException;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JacksonUtilExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtilExceptionHandler.class);

    @ExceptionHandler({JacksonUtilException.class})
    public AjaxResp<Void> handleBaseException(JacksonUtilException jue) {
        logger.error(jue.getMessage(), jue);
        return AjaxResp.of(AjaxCode.JACKSON_UTIL_ERROR, jue.getMessage());
    }

}
