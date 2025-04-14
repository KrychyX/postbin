package com.example.demo.config;

import com.example.demo.model.Article;
import com.example.demo.utils.CacheUtil;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки кэширования в приложении.
 * Определяет бин кэшей, используемые в различных сервисах приложения.
 */
@Configuration
public class CacheConfig {

    /**
     * Создает и возвращает кэш для хранения списков статей, сгруппированных по авторам.
     * Кэш использует алгоритм LRU (Least Recently Used) с максимальным размером 100 записей.
     * При достижении лимита самые давно неиспользуемые записи будут автоматически удаляться.
     *
     * @return новый экземпляр {@link CacheUtil}, настроенный для хранения:
     *         ключ - имя автора (String),
     *         значение - список статей автора (List&lt;Article&gt)
     * @see CacheUtil
     * @see Article
     */
    @Bean
    public CacheUtil<String, List<Article>> articleCacheByAuthor() {
        return new CacheUtil<>(10);
    }
}