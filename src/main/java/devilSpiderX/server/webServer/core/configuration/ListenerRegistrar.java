package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.listener.HttpSessionRegister;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ListenerRegistrar implements WebMvcConfigurer {
    @Bean
    public ServletListenerRegistrationBean<HttpSessionRegister> regHttpSessionRegister(HttpSessionRegister listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }
}
