package devilSpiderX.server.webServer.common.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 一个异常：代表当前不是获取静态资源时为空
 *
 * @author DevilSpiderX
 */
@Getter
public class HTTPException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2355026029568871397L;

    private final HttpStatus status;

    public HTTPException(HttpStatus status) {
        super();
        this.status = status;
    }

}
