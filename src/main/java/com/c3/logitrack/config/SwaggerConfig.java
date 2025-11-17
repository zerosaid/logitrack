package com.c3.logitrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiTrack S.A. - Sistema de Gestión y Auditoría de Bodegas")
                        .version("1.0.0")
                        .description("""
                                Backend RESTful para la gestión centralizada de bodegas, productos, movimientos de inventario y auditoría completa de operaciones.
                                
                                **Características clave:**
                                - CRUD completo de bodegas y productos
                                - Registro de entradas, salidas y transferencias
                                - Auditoría automática con JPA EntityListeners
                                - Seguridad con JWT y roles (ADMIN / EMPLEADO)
                                - Reportes avanzados y filtros
                                - Documentación interactiva con Swagger UI
                                """)
                        .contact(new Contact()
                                .name("LogiTrack S.A.")
                                .email("soporte@logitrack.com")
                                .url("https://github.com/zerosaid/logitrack"))
                        .license(new License()
                                .name("Propiedad Privada - LogiTrack S.A.")
                                .url("https://github.com/zerosaid/logitrack/blob/main/LICENSE")))
                
                // Servidor local
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local")
                ))

                // Seguridad global: JWT
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        **Autenticación JWT**
                                        
                                        1. Usa `POST /auth/login` con credenciales.
                                        2. Copia el token del campo `token`.
                                        3. Haz clic en **Authorize** e ingresa: `Bearer <token>`
                                        
                                        **Roles:**
                                        - `ADMIN`: Acceso completo
                                        - `EMPLEADO`: Acceso limitado (solo lectura y movimientos)
                                        """)
                        )
                )

                // Aplicar seguridad globalmente (excepto /auth/**)
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }
}