package devilSpiderX.server.webServer.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import devilSpiderX.server.webServer.core.jackson.JacksonUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer mapperBuilderCustomizer() {
        return (builder) -> {
            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                    .simpleDateFormat(JacksonUtil.STANDARD_FORMAT)
                    .featuresToDisable(
                            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                            SerializationFeature.FAIL_ON_EMPTY_BEANS,
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                    );
        };
    }
}
