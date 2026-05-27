package com.app.resumemaker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "email_verification_queue";

    @Bean
    public Queue emailVerificationQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}
