package devilSpiderX.server.webServer.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        final var array = new CorsRegistration[]{
                registry.addMapping("/api/**"),
                registry.addMapping("/user/**"),
                registry.addMapping("/websocket/**")
        };
        for (final var r : array) {
            r.allowCredentials(false)
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "OPTIONS", "HEAD")
                    .allowedHeaders("*")
                    .exposedHeaders("*");
        }
    }

}
