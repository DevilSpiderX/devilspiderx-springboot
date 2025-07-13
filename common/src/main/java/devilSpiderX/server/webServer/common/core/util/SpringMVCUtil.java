package devilSpiderX.server.webServer.common.core.util;

import devilSpiderX.server.webServer.common.core.exception.NotWebContextException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author DevilSpiderX
 */
public final class SpringMVCUtil {

    private SpringMVCUtil() {
    }

    /**
     * 获取当前会话的 request
     *
     * @return request
     */
    public static HttpServletRequest getRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest");
    }

    /**
     * 获取当前会话的 response
     *
     * @return response
     */
    public static HttpServletResponse getResponse() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getResponse();
        }
        throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest");
    }

    /**
     * 判断当前是否处于 Web 上下文中
     *
     * @return true 表示处于 Web 上下文中；否则不在
     */
    public static boolean isWeb() {
        return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes;
    }

}
