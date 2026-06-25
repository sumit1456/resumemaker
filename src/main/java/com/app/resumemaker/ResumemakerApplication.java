package com.app.resumemaker;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class ResumemakerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ResumemakerApplication.class);
    private static final String DEPLOY_MARKER = "cors-security-cleanup-2026-05-29-01";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ResumemakerApplication.class);
        String port = System.getenv("PORT"); // Dynamic port for Render
        if (port != null) {
            app.setDefaultProperties(Map.of("server.port", port));
        }
        app.run(args);
        logger.info("ResumeMaker backend deploy marker: {}", DEPLOY_MARKER);
        logger.info("ResumeMaker backend runtime port env PORT={}", port == null ? "not-set" : port);
        System.out.println("=======================================");
        System.out.println("Application has been started " + DEPLOY_MARKER);
        System.out.println("=======================================");
    }

    // Password encoder for registration/login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private com.app.resumemaker.security.JwtAuthenticationFilter jwtAuthFilter;

    // Security configuration — disable CSRF and allow all requests including
    // Swagger
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/signup",
                                "/v2/signup",
                                "/login",
                                "/google-login",
                                "/verify",
                                "/ping",
                                "/pdf-extraction-config",
                                "/chat-portfolio",
                                "/analyze",
                                "/create-report",
                                "/enhanceResume")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        logger.info(
                "CORS configured. allowedOriginPatterns=*, allowedMethods=GET,POST,PUT,DELETE,OPTIONS, allowCredentials=true");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Configuration
    public class RedisConfig {

        @Bean
        public RedisTemplate<String, Object> redisTemplate(
                RedisConnectionFactory connectionFactory) {

            RedisTemplate<String, Object> template = new RedisTemplate<>();

            template.setConnectionFactory(connectionFactory);

            // Key serializer
            template.setKeySerializer(new StringRedisSerializer());

            // Value serializer
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

            // Hash serializers
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

            template.afterPropertiesSet();

            return template;
        }
    }

}
