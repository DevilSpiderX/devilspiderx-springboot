package devilSpiderX.server.webServer.server.core.configuration;

import devilSpiderX.server.webServer.server.core.service.OS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSConfigurer {

    @Bean
    public OS dsxOS() {
        return OS.Factory.getOS();
    }
}
