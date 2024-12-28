package devilSpiderX.server.webServer.module.saToken.configuration;

import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/api/**");
    }
}
