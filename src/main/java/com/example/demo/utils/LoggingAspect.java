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
 * <p>Provides conditional cross-cutting logging functionality for:
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
    private static final boolean ENABLE_ARGS_LOGGING = false;
    private static final boolean ENABLE_RESULT_LOGGING = false;

    /**
     * Logs method entry before controller method execution.
     * Only logs method signature unless args logging is enabled.
     */
    @Before("execution(* com.example.demo.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            String methodInfo = joinPoint.getSignature().toShortString();
            if (ENABLE_ARGS_LOGGING) {
                logger.info("Method called: {} with args: {}",
                        methodInfo, sanitizeArguments(joinPoint.getArgs()));
            } else {
                logger.info("Method called: {}", methodInfo);
            }
        }
    }

    /**
     * Logs successful method completion in controllers.
     * Only logs if result logging is enabled.
     */
    @AfterReturning(
            pointcut = "execution(* com.example.demo.controller.*.*(..))",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled() && ENABLE_RESULT_LOGGING) {
            logger.info("Method completed: {}",
                    joinPoint.getSignature().toShortString());
        }
    }

    /**
     * Logs exceptions thrown by any method in the application.
     * Always logs errors but sanitizes sensitive information.
     */
    @AfterThrowing(
            pointcut = "execution(* com.example.demo..*.*(..))",
            throwing = "error"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Error in method: {}. Reason: {}",
                    joinPoint.getSignature().toShortString(),
                    error.getMessage());
        }
    }

    /**
     * Sanitizes arguments to prevent logging sensitive data.
     */
    private Object[] sanitizeArguments(Object[] args) {
        if (args == null) {
            return null;
        }

        Object[] sanitized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && isSensitiveType(args[i].getClass())) {
                sanitized[i] = "[PROTECTED]";
            } else {
                sanitized[i] = args[i];
            }
        }
        return sanitized;
    }

    /**
     * Checks if the type might contain sensitive data.
     */
    private boolean isSensitiveType(Class<?> type) {
        return type.getName().contains("Password")
                || type.getName().contains("Secret")
                || type.getName().contains("Token")
                || type.getName().contains("Credentials");
    }
}