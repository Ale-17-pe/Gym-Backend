package com.gym.backend.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${gym.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("URL del servidor en entorno de desarrollo");

        Contact contact = new Contact();
        contact.setEmail("gym@backend.com");
        contact.setName("Gym Backend");
        contact.setUrl("https://www.gymbackend.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Gym Management API")
                .version("1.0")
                .contact(contact)
                .description("Esta API expone endpoints para el sistema de gestión del gimnasio.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}