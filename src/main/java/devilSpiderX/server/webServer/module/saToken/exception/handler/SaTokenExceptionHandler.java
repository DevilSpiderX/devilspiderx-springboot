package devilSpiderX.server.webServer.module.saToken.exception.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SaTokenExceptionHandler {
    @ExceptionHandler({NotLoginException.class})
    public AjaxResp<?> handleNotLoginException(NotLoginException nle) {
        var result = AjaxResp.notLogin();
        switch (nle.getType()) {
            case NotLoginException.INVALID_TOKEN -> result = AjaxResp.notLogin("用户请求携带无效token");
            case NotLoginException.TOKEN_TIMEOUT -> result = AjaxResp.notLogin("用户登录已过期");
            case NotLoginException.BE_REPLACED -> result = AjaxResp.notLogin("用户登录已被顶下线");
            case NotLoginException.KICK_OUT -> result = AjaxResp.notLogin("用户已被踢下线");
        }
        return result;
    }

    @ExceptionHandler({NotRoleException.class})
    public AjaxResp<?> handleNotRoleException(NotRoleException nre) {
        return AjaxResp.notRole(nre.getRole());
    }

    @ExceptionHandler({NotPermissionException.class})
    public AjaxResp<?> handleNotRoleException(NotPermissionException npe) {
        return AjaxResp.notPermission(npe.getPermission());
    }
}
