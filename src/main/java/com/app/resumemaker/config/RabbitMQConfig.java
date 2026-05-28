package com.app.resumemaker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

    @Value("${RABBITMQ_QUEUE_RESUMEMAKER:email_resumemaker_queue}")
    private String queueName;

    @Bean
    public Queue emailVerificationQueue() {
        return new Queue(queueName, true);
    }
}
