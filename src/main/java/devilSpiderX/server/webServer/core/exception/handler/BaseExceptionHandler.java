package devilSpiderX.server.webServer.core.exception.handler;

import devilSpiderX.server.webServer.core.exception.BaseException;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler({BaseException.class})
    public AjaxResp<?> handleBaseException(BaseException be) {
        return AjaxResp.of(be.getCode(), be.getMessage());
    }
}
