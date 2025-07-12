package devilSpiderX.server.webServer.core.configuration;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.config.SaTokenConfig;
import devilSpiderX.server.webServer.module.satoken.StpKit;
import devilSpiderX.server.webServer.module.satoken.properties.StpKitProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class Knife4jConfig {

    private final SaTokenConfig saTokenConfig;
    private final StpKitProperties stpKitProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(customInfo());
    }

    @Bean
    public Info customInfo() {
        final var pkg = this.getClass()
                .getPackage();
        return new Info()
                .title("DSX-Server服务后端")
                .version(pkg.getImplementationVersion())
                .contact(new Contact().name("DevilSpiderX")
                        .email("775716441@qq.com"));
    }

    @Bean
    public GroupedOpenApi defaultGroup() {
        return GroupedOpenApi.builder()
                .group("default")
                .displayName("默认")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/admin/**")
                .packagesToScan("devilSpiderX.server.webServer")
                .addOpenApiCustomizer(openApi -> {
                    openApi.schemaRequirement(StpKit.USER_TYPE, userSecurityScheme());
                    openApi.schemaRequirement(StpKit.ADMIN_TYPE, adminSecurityScheme());
                })
                .addOperationCustomizer(saCheckLoginOperationCustomizer())
                .build();
    }

    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("管理员")
                .pathsToMatch("/api/admin/**")
                .packagesToScan("devilSpiderX.server.webServer")
                .addOpenApiCustomizer(openApi -> {
                    openApi.schemaRequirement(StpKit.ADMIN_TYPE, adminSecurityScheme());
                })
                .addOperationCustomizer(saCheckLoginOperationCustomizer())
                .build();
    }

    @Bean
    public SecurityScheme userSecurityScheme() {
        final var tokenName = saTokenConfig.getTokenName();
        return new SecurityScheme()
                .name(tokenName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
    }

    @Bean
    public SecurityScheme adminSecurityScheme() {
        final var tokenName = stpKitProperties.getAdmin()
                .getTokenName();
        return new SecurityScheme()
                .name(tokenName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
    }

    @Bean
    public OperationCustomizer saCheckLoginOperationCustomizer() {
        return (operation, handlerMethod) -> {
            final var elementList = List.of(handlerMethod.getBeanType(), handlerMethod.getMethod());
            for (final var element : elementList) {
                final var ignoreAnno = AnnotatedElementUtils.getMergedAnnotation(element, SaIgnore.class);
                if (ignoreAnno != null) {
                    return operation;
                }
            }
            final List<SecurityRequirement> srList = new ArrayList<>();
            for (final var element : elementList) {
                final var orAnno = AnnotatedElementUtils.getMergedAnnotation(element, SaCheckOr.class);
                if (orAnno != null && ArrayUtils.isNotEmpty(orAnno.login())) {
                    for (final var loginAnno : orAnno.login()) {
                        var type = StpKit.USER_TYPE;
                        if (StringUtils.isNotBlank(loginAnno.type())) {
                            type = loginAnno.type();
                        }
                        srList.add(new SecurityRequirement().addList(type));
                    }
                }

                final var loginAnnoSet = AnnotatedElementUtils.getAllMergedAnnotations(element, SaCheckLogin.class);
                if (loginAnnoSet.isEmpty()) {
                    continue;
                }
                if (srList.isEmpty()) {
                    srList.add(new SecurityRequirement());
                }
                for (final var loginAnno : loginAnnoSet) {
                    var type = StpKit.USER_TYPE;
                    if (StringUtils.isNotBlank(loginAnno.type())) {
                        type = loginAnno.type();
                    }
                    for (final var sr : srList) {
                        sr.addList(type);
                    }
                }
                break;
            }
            for (final var sr : new HashSet<>(srList)) {
                operation.addSecurityItem(sr);
            }
            return operation;
        };
    }

}
