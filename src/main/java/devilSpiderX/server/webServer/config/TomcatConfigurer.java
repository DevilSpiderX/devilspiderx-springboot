package devilSpiderX.server.webServer.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfigurer {
    @Value("${server.http_port}")
    private int http_port;
    @Value("${server.https_port}")
    private int https_port;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory(https_port) {
            @Override
            protected void postProcessContext(Context ctx) {
                SecurityCollection collection = new SecurityCollection();
                collection.setName("SSL");
                collection.addPattern("/*");

                SecurityConstraint constraint = new SecurityConstraint();
                constraint.addCollection(collection);
                constraint.setUserConstraint("CONFIDENTIAL");

                ctx.addConstraint(constraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector();
        connector.setScheme("http");
        connector.setPort(http_port);
        connector.setRedirectPort(https_port);
        return connector;
    }
}
