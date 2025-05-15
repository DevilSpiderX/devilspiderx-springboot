package devilSpiderX.server.webServer.core.configuration;

import devilSpiderX.server.webServer.core.util.BytesHttpMessageConverter;
import devilSpiderX.server.webServer.core.util.FormToJSONHttpMessageConverter;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MessageConvertersConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
        converters.add(new BytesHttpMessageConverter());
        converters.add(new FormToJSONHttpMessageConverter());
    }

}
