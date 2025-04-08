package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.util.BytesHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.FormToJSONHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.JacksonUtil;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
        removeDefaultMappingJackson2HttpMessageConverter(converters);
        converters.add(getMappingJackson2HttpMessageConverter());
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

    private void removeDefaultMappingJackson2HttpMessageConverter(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        final var array = new CorsRegistration[]{
                registry.addMapping("/api/**"),
                registry.addMapping("/user/**"),
                registry.addMapping("/websocket/**")
        };
        for (final var r : array) {
            r.allowCredentials(false)
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "OPTIONS", "HEAD")
                    .allowedHeaders("*")
                    .exposedHeaders("*");
        }
    }
}
