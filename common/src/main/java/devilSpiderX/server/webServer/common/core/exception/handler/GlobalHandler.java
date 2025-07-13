package devilSpiderX.server.webServer.common.core.exception.handler;

import devilSpiderX.server.webServer.common.core.exception.BaseException;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * @author DevilSpiderX
 */
@RestControllerAdvice
@Order
public class GlobalHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalHandler.class);

    @ExceptionHandler({BaseException.class})
    public CommonResult<Void> handleException(BaseException be) {
        return CommonResult.of(be.getCode(), be.getMessage());
    }

    @ExceptionHandler({HandlerMethodValidationException.class})
    public CommonResult<Void> handlerMethodValidationException(HandlerMethodValidationException e) {
        final var firstError = e.getAllErrors()
                .getFirst();
        if (firstError != null) {
            return CommonResult.of(ResultCode.IllegalArgument, firstError.getDefaultMessage());
        }

        return CommonResult.of(ResultCode.IllegalArgument, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResult<Void> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final var firstFieldError = e.getFieldErrors()
                .getFirst();
        if (firstFieldError != null) {
            return CommonResult.of(ResultCode.IllegalArgument, firstFieldError.getDefaultMessage());
        }

        final var firstError = e.getAllErrors()
                .getFirst();
        if (firstError != null) {
            return CommonResult.of(ResultCode.IllegalArgument, firstError.getDefaultMessage());
        }

        return CommonResult.of(ResultCode.IllegalArgument, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public CommonResult<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return CommonResult.of(ResultCode.IllegalArgument, e.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class})
    public CommonResult<Void> handleIllegalStateException(IllegalStateException e) {
        return CommonResult.of(ResultCode.IllegalState, e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public CommonResult<Void> handleException(RuntimeException re) {
        logger.error(re.getMessage(), re);
        return CommonResult.of(ResultCode.Error, re.getMessage());
    }
}
