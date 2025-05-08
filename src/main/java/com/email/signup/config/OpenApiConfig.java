package com.email.signup.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "rateLimit";
        return new OpenAPI()
                .info(new Info()
                        .title("Email Signup Service API")
                        .description("""
                            API documentation for the Email Signup Service. This service handles email subscriptions,
                            confirmations, and subscriber management.

                            ## Rate Limiting
                            - All endpoints are rate limited to 5 requests per minute per IP address
                            - Exceeding the rate limit will result in a 429 Too Many Requests response

                            ## Security
                            - Input validation is performed on all endpoints
                            - XSS prevention is enabled
                            - Email addresses are validated for format and disposable domains

                            ## Testing
                            - Use the Try it out button to test endpoints
                            - For email confirmation, use the token from the confirmation email
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Email Signup Team")
                                .email("danielthai6570@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.yourdomain.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Rate limiting is applied per IP address")));
    }
}
