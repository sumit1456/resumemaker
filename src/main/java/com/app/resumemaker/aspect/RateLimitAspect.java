package com.app.resumemaker.aspect;

import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.app.resumemaker.annotation.RateLimited;
import com.app.resumemaker.exception.RateLimitExceededException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
@Aspect
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = "rate_limit:" + joinPoint.getSignature().getName() + ":" + email;

        Long requests = redisTemplate.opsForValue().increment(key);
        if (requests == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(rateLimited.window()));
        }

        if (requests > rateLimited.limit()) {
            Long ttl = redisTemplate.getExpire(key);
            throw new RateLimitExceededException("Rate limit exceeded. Try again in " + ttl + " seconds.");
        }

        return joinPoint.proceed();
    }

}
