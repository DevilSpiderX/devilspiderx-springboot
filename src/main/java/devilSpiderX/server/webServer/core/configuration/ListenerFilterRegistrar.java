package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.filter.GlobalFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerFilterRegistrar {
    @Bean
    public FilterRegistrationBean<GlobalFilter> regGlobalFilter(GlobalFilter filter) {
        return new FilterRegistrationBean<>(filter);
    }
}
