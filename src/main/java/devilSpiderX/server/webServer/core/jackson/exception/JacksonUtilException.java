package devilSpiderX.server.webServer.core.jackson.exception;

public class JacksonUtilException extends RuntimeException {
    public JacksonUtilException() {
    }

    public JacksonUtilException(String message) {
        super(message);
    }

    public JacksonUtilException(Throwable cause) {
        super(cause);
    }

    public JacksonUtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
