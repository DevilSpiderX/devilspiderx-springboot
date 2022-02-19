package devilSpiderX.server.webServer.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer, ErrorPageRegistrar {
    private final MyConfig config;

    public MyWebAppConfigurer(MyConfig config) {
        this.config = config;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:" + config.getSTATIC_LOCATION() + "/";
        registry.addResourceHandler("/**").addResourceLocations(path);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, fastJsonHttpMessageConverter());
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    private @NotNull FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);
        return converter;
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
