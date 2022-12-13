package devilSpiderX.server.webServer.core.configuration;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.BytesHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.FormToJSONHttpMessageConverter;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer, ErrorPageRegistrar {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, fastJsonHttpMessageConverter());
        converters.add(new BytesHttpMessageConverter());
        converters.add(new FormToJSONHttpMessageConverter());
    }

    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setWriterFeatures(JSONWriter.Feature.WriteBigDecimalAsPlain);
        config.setReaderFeatures(JSONReader.Feature.UseBigDecimalForDoubles, JSONReader.Feature.IgnoreSetNullValue);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);
        return converter;
    }

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage[] pages = {
                new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"),
                new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"),
                new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/error/405"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500")
        };
        registry.addErrorPages(pages);
    }
}
