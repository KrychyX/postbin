package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 *
 * <p>Defines the base API information and server configuration that will be displayed
 * in the generated OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates and configures the OpenAPI documentation bean.
     *
     * <p>Configures:
     * <ul>
     *   <li>API server location (localhost:8080)</li>
     *   <li>API title ("Postbin")</li>
     * </ul>
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .servers(
                    List.of(
                            new Server().url("http://localhost:8080")
                    )
                )
                .info(
                        new Info().title("Postbin")
                );
    }

}
