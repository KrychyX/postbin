package com.example.demo.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method execution in controllers.
 *
 * <p>Provides cross-cutting logging functionality for:
 * <ul>
 *   <li>Method entry (before execution)</li>
 *   <li>Successful method completion</li>
 *   <li>Method exceptions</li>
 * </ul>
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Logs method entry before controller method execution.
     *
     * @param joinPoint the join point containing method information
     */
    @Before("execution(* com.example.demo.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Вызов метода: {} с аргументами: {}",
                joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    /**
     * Logs successful method completion in controllers.
     *
     * @param joinPoint the join point containing method information
     * @param result the method return value
     */
    @AfterReturning(pointcut = "execution(* com.example.demo.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Метод {} успешно выполнен. Результат: {}",
                joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs exceptions thrown by any method in the application.
     *
     * @param joinPoint the join point containing method information
     * @param error the thrown exception
     */
    @AfterThrowing(pointcut = "execution(* com.example.demo..*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Ошибка в методе: {}. Причина: {}",
                joinPoint.getSignature().toShortString(), error.getMessage(), error);
    }
}