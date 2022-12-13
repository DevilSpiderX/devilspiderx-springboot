package devilSpiderX.server.webServer.core.exception.handler;

import devilSpiderX.server.webServer.core.exception.NotLoginException;
import devilSpiderX.server.webServer.core.exception.NotAdminException;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotLoginException.class})
    public AjaxResp<Void> handleNotLoginException() {
        return AjaxResp.notLogin();
    }

    @ExceptionHandler({NotAdminException.class})
    public AjaxResp<Void> handleNotAdminException() {
        return AjaxResp.notAdmin();
    }
}
