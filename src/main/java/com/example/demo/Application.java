package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения, который запускает Spring Boot приложение.
 * Использует аннотацию {@link SpringBootApplication} для автоматической настройки
 * и сканирования компонентов приложения.
 */
@SpringBootApplication
public class Application {

    /**
    * Основной метод, который запускает Spring Boot приложение.
    */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}