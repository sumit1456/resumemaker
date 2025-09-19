package com.app.resumemaker;



import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@SpringBootApplication
public class ResumemakerApplication {
    // comment to test
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ResumemakerApplication.class);
        String port = System.getenv("PORT"); // Dynamic port for Render
        if (port != null) {
            app.setDefaultProperties(Map.of("server.port", port));
        }
        app.run(args);
    }

    // Password encoder for user registration / login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Swagger/OpenAPI configuration
    

    // Security configuration
   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );
    return http.build();
}

    // CORS configuration changes
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://localhost:5173", "https://localhost:5173/createresume",// for local dev
                            "https://resumemaker-frontend-4ufl.onrender.com",
                            "https://resumemaker-1.onrender.com/createresume",
                            "https://resumemaker-1.onrender.com/${resumeId}/saveprojects",
                            ""
                            // replace with deployed React URL
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
