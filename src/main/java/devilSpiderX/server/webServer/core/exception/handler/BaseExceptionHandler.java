package devilSpiderX.server.webServer.core.exception.handler;

import devilSpiderX.server.webServer.core.exception.BaseException;
import devilSpiderX.server.webServer.core.util.AjaxCode;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler({BaseException.class})
    public AjaxResp<Void> handleBaseException(BaseException be) {
        return AjaxResp.of(be.getCode(), be.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public AjaxResp<Void> handleRuntimeException(RuntimeException re) {
        return AjaxResp.of(AjaxCode.ERROR, re.getMessage());
    }
}
