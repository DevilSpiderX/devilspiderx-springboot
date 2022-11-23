package devilSpiderX.server.webServer.configuration;

import devilSpiderX.server.webServer.filter.AdminFilter;
import devilSpiderX.server.webServer.filter.UserFilter;
import devilSpiderX.server.webServer.listener.HttpSessionRegister;
import devilSpiderX.server.webServer.listener.MyRequestListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterAndWebListener {

    @Bean
    public FilterRegistrationBean<AdminFilter> regAdminFilter(AdminFilter filter) {
        FilterRegistrationBean<AdminFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setName("AdminFilter");
        bean.setOrder(1);
        //需要用户是管理员才能用的URL列表
        bean.addUrlPatterns("/api/admin/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<UserFilter> regUserFilter(UserFilter filter) {
        FilterRegistrationBean<UserFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setName("UserFilter");
        bean.setOrder(0);
        //需要用户登录才能用的URL列表
        bean.addUrlPatterns(
                "/api/admin/*",
                "/api/query/*",
                "/api/v2ray",
                "/api/service/shutdown",
                "/api/ServerInfo/*"
        );
        return bean;
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
