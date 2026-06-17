package mp.gov.ftms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI ftmsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MP Government FTMS API")
                        .version("1.0.0")
                        .description("Financial Transaction Management APIs for departments, budgets, beneficiaries, approvals, reconciliation and audit trails.")
                        .contact(new Contact().name("Government of Madhya Pradesh").email("ftms-support@mp.gov.in")))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

