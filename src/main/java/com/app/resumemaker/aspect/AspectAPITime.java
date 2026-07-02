package com.app.resumemaker.aspect;

import com.app.resumemaker.annotation.APITime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AspectAPITime {

    private static final Logger logger = LoggerFactory.getLogger(AspectAPITime.class);

    @Around("@annotation(APITime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        logger.info("{} took {} ms", joinPoint.getSignature().toShortString(), (end - start));

        return result;
    }

}
