package devilSpiderX.server.webServer.module.satoken.configuration;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @PostConstruct
    public void rewriteSaStrategy() {
        SaAnnotationStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/api/**");
    }

}
