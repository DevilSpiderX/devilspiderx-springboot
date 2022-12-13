package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.interceptor.AdminInterceptor;
import devilSpiderX.server.webServer.core.interceptor.LoginInterceptor;
import devilSpiderX.server.webServer.core.listener.HttpSessionRegister;
import devilSpiderX.server.webServer.core.listener.MyRequestListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FILRegistrar implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var loginInterceptor = registry.addInterceptor(new LoginInterceptor());
        loginInterceptor.order(0);
        loginInterceptor.addPathPatterns(
                "/api/admin/**",
                "/api/query/**",
                "/api/ServerInfo/**"
        );

        var adminInterceptor = registry.addInterceptor(new AdminInterceptor());
        adminInterceptor.order(1);
        adminInterceptor.addPathPatterns(
                "/api/admin/**"
        );
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionRegister> regHttpSessionRegister(HttpSessionRegister listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }

    @Bean
    public ServletListenerRegistrationBean<MyRequestListener> regMyRequestListener(MyRequestListener listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }
}
