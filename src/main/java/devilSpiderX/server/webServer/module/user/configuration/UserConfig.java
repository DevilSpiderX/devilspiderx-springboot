package devilSpiderX.server.webServer.module.user.configuration;

import devilSpiderX.server.webServer.module.user.interceptor.AdminInterceptor;
import devilSpiderX.server.webServer.module.user.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    public UserConfig(LoginInterceptor loginInterceptor,
                      AdminInterceptor adminInterceptor) {
        this.loginInterceptor = loginInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .order(0)
                .addPathPatterns(
                        "/api/admin/**",
                        "/api/query/**",
                        "/api/ServerInfo/**"
                );

        registry.addInterceptor(adminInterceptor)
                .order(1)
                .addPathPatterns(
                        "/api/admin/**"
                );
    }
}
