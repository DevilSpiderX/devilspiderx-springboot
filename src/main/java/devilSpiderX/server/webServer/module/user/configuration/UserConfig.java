package devilSpiderX.server.webServer.module.user.configuration;

import devilSpiderX.server.webServer.module.user.interceptor.AdminInterceptor;
import devilSpiderX.server.webServer.module.user.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var loginInterceptorReg = registry.addInterceptor(new LoginInterceptor());
        loginInterceptorReg.order(0);
        loginInterceptorReg.addPathPatterns(
                "/api/admin/**",
                "/api/query/**",
                "/api/ServerInfo/**"
        );

        var adminInterceptorReg = registry.addInterceptor(new AdminInterceptor());
        adminInterceptorReg.order(1);
        adminInterceptorReg.addPathPatterns(
                "/api/admin/**"
        );
    }
}
