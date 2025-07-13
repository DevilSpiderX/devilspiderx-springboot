package devilSpiderX.server.webServer.server.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static devilSpiderX.server.webServer.common.core.constant.JacksonConstant.STANDARD_DATE_TIME_FORMAT;
import static devilSpiderX.server.webServer.common.core.constant.JacksonConstant.STANDARD_TIME_MODULE;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer mapperBuilderCustomizer() {
        return (builder) ->
                builder.serializationInclusion(JsonInclude.Include.ALWAYS)
                        .simpleDateFormat(STANDARD_DATE_TIME_FORMAT)
                        .featuresToDisable(
                                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                        )
                        .modules(STANDARD_TIME_MODULE);
    }

}
