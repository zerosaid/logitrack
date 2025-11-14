package com.c3.logitrack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LogitrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogitrackApplication.class, args);
	}

	@Bean
    CommandLineRunner testHash(PasswordEncoder passwordEncoder) {
        return args -> {
            String hash = passwordEncoder.encode("adminPass01");
            System.out.println("HASH CORRECTO PARA 'adminPass01': " + hash);

            boolean match = passwordEncoder.matches("adminPass01",
                "$2a$10$SBAgroaxJBDh3EiXYymSwO8iIOZ8XE1ZuYLyTGuZhHy1IGqkYc0uu");
            System.out.println("¿EL HASH DE LA BD COINCIDE? " + match);
        };
    }
}
