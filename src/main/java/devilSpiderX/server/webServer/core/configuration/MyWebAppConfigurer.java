package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.util.BytesHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.FormToJSONHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.JacksonUtil;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer, ErrorPageRegistrar {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addFirst(getMappingJackson2HttpMessageConverter());
        converters.add(new BytesHttpMessageConverter());
        converters.add(new FormToJSONHttpMessageConverter());
    }

    private MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        final var converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(JacksonUtil.MAPPER);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        final List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);
        return converter;
    }

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        final ErrorPage[] pages = {
                new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"),
                new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"),
                new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/error/405"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500")
        };
        registry.addErrorPages(pages);
    }
}
