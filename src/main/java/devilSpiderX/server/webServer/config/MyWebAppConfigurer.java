package devilSpiderX.server.webServer.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer, ErrorPageRegistrar {
    private final MyConfig config;

    public MyWebAppConfigurer(MyConfig config) {
        this.config = config;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + Paths.get(config.getSTATIC_LOCATION()).toAbsolutePath() + "/";
        registry.addResourceHandler("/**").addResourceLocations(path);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        for (int i = 0; i < config.getERROR_PAGES().size(); i++) {
            JSONObject pageConfig = config.getERROR_PAGES().getJSONObject(i);
            ErrorPage errorPage = new ErrorPage(HttpStatus.valueOf(pageConfig.getInteger("code")),
                    pageConfig.getString("location"));
            registry.addErrorPages(errorPage);
        }
    }
}
