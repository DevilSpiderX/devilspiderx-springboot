package devilSpiderX.server.webServer.common.core.util;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ClientIpUtil {
    private static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile(
            "(?i:for)=\"?\\s*(([\\d.]+)|\\[([\\da-fA-F:]+)])(:\\d{1,5})?\\s*\"?");


    private ClientIpUtil() {
    }

    public static @Nonnull String getClientIp() {
        final var request = SpringMVCUtil.getRequest();
        return getClientIp(request);
    }

    public static @Nonnull String getClientIp(final @Nonnull HttpServletRequest request) {
        Assert.notNull(request, "request must not be null");
        final var forwardedHeader = request.getHeader("Forwarded");
        if (StringUtils.hasText(forwardedHeader)) {
            final var forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0];
            final var matcher = FORWARDED_FOR_PATTERN.matcher(forwardedToUse);
            if (matcher.find()) {
                final var value = matcher.group(2);
                if (StringUtils.hasText(value)) {
                    return value;
                }
                return matcher.group(3);
            }
        }

        final var forHeader = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forHeader)) {
            return StringUtils.tokenizeToStringArray(forHeader, ",")[0];
        }

        return request.getRemoteAddr();
    }
}
