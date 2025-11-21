package com.c3.logitrack.config;

import com.c3.logitrack.security.JwtAuthEntryPoint;
import com.c3.logitrack.security.JwtAuthFilter;
import com.c3.logitrack.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint,
                          JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // Permitir OPTIONS para CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth públicas
                        .requestMatchers("/api/auth/login", "/api/usuarios/login",
                                "/api/auth/register", "/api/usuarios/register").permitAll()

                        // PERMITIR TODO EL FRONTEND (FIX IMPORTANTE)
                        .requestMatchers("/fronted/**").permitAll()

                        // Archivos estáticos comunes
                        .requestMatchers("/", "/index.html", "/login.html", "/dashboard.html",
                                "/register.html", "/*.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**",
                                "/fonts/**", "/assets/**", "/icon/**").permitAll()

                        .requestMatchers("/favicon.ico", "/robots.txt").permitAll()

                        // Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Endpoints públicos GET
                        .requestMatchers(HttpMethod.GET,
                                "/api/bodegas/**", "/api/productos/**", "/api/usuarios/**").permitAll()

                        // Movimientos requieren autenticación
                        .requestMatchers("/api/movimientos/**").authenticated()

                        // Solo ADMIN puede modificar
                        .requestMatchers(HttpMethod.POST,
                                "/api/bodegas/**", "/api/productos/**", "/api/movimientos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/bodegas/**", "/api/productos/**", "/api/movimientos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/bodegas/**", "/api/productos/**", "/api/movimientos/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
