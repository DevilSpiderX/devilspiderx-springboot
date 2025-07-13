package devilSpiderX.server.webServer.common.module.satoken.exception.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static devilSpiderX.server.webServer.common.core.util.SimpleStringFormatter.format;

/**
 * @author DevilSpiderX
 */
@RestControllerAdvice
@Order(2)
public class SaTokenExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SaTokenExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    public CommonResult<Void> handlerNotLoginException(NotLoginException ex) {
        final var type = ex.getType();

        return switch (type) {
            case NotLoginException.BE_REPLACED -> CommonResult.of(ResultCode.BeReplaced);
            case NotLoginException.KICK_OUT -> CommonResult.of(ResultCode.KickOut);
            // 与activeTimeout有关
            case NotLoginException.TOKEN_FREEZE -> CommonResult.of(ResultCode.TokenFreeze);
            default -> CommonResult.of(ResultCode.NotLogin);
        };
    }

    @ExceptionHandler(NotRoleException.class)
    public CommonResult<String> handlerNotRoleException(NotRoleException ex) {
        final var code = ResultCode.NotRole;
        return CommonResult.of(code.getCode(), code.getMessage(), ex.getRole());
    }

    @ExceptionHandler(NotPermissionException.class)
    public CommonResult<String> handlerNotPermissionException(NotPermissionException ex) {
        final var code = ResultCode.NotPermission;
        return CommonResult.of(code.getCode(), code.getMessage(), ex.getPermission());
    }

    @ExceptionHandler(SaTokenException.class)
    public CommonResult<Void> handlerSaTokenException(SaTokenException ex) {
        final var msg = format("code:{0}, msg:{1}", ex.getCode(), ex.getMessage());

        logger.error(msg, ex);
        return CommonResult.of(ResultCode.Error, format("SaToken错误:{}", ex.getCode()));
    }
}
