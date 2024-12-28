package devilSpiderX.server.webServer.core.filter;

import devilSpiderX.server.webServer.core.property.ApplicationProperties;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalFilter extends OncePerRequestFilter {
    private final ApplicationProperties applicationProperties;

    public GlobalFilter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        addApplicationInfo(response);
        filterChain.doFilter(request, response);
    }

    private void addApplicationInfo(HttpServletResponse response) {
        response.addHeader("Application-Title", applicationProperties.getTitle());
        response.addHeader("Application-Version", applicationProperties.getVersion());
    }
}
