package com.example.demo.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration class for asynchronous task execution in the application.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Creates and configures a dedicated thread pool executor for log processing tasks.
     */
    @Bean(name = "logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);      // Базовое количество потоков
        executor.setMaxPoolSize(8);       // Максимальное при нагрузке
        executor.setQueueCapacity(50);    // Размер очереди задач
        executor.setThreadNamePrefix("LogTask-"); // Префикс для логирования
        executor.initialize();
        return executor;
    }
}