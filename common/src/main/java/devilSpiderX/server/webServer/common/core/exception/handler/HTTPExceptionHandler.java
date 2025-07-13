package devilSpiderX.server.webServer.common.core.exception.handler;

import devilSpiderX.server.webServer.common.core.exception.HTTPException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author DevilSpiderX
 */
@RestControllerAdvice
@Order(1)
public class HTTPExceptionHandler {

    @ExceptionHandler({HTTPException.class})
    public ResponseEntity<Void> handleException(HTTPException ex) {
        return ResponseEntity.status(ex.getStatus())
                .build();
    }

}
