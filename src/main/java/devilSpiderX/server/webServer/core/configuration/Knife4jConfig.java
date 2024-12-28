package devilSpiderX.server.webServer.core.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(customInfo());
    }

    private Info customInfo() {
        final var pkg = this.getClass().getPackage();
        return new Info()
                .title("DSX-Server服务后端")
                .version(pkg.getImplementationVersion())
                .contact(new Contact().email("775716441@qq.com"));
    }

}
