package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.filter.GlobalFilter;
import devilSpiderX.server.webServer.core.listener.HttpSessionRegister;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerFilterRegistrar {
    @Bean
    public ServletListenerRegistrationBean<HttpSessionRegister> regHttpSessionRegister(HttpSessionRegister listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }

    @Bean
    public FilterRegistrationBean<GlobalFilter> regGlobalFilter(GlobalFilter filter) {
        return new FilterRegistrationBean<>(filter);
    }
}
