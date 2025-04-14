package com.example.demo.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Потокобезопасный LRU-кэш на основе LinkedHashMap с логирование операций.
 *
 * @param <K> тип ключа кэша
 * @param <V> тип значения кэша
 */
public class CacheUtil<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtil.class);

    private final LinkedHashMap<K, V> cache;
    private final ReentrantLock lock = new ReentrantLock();
    private final int maxSize;

    /**
     * Создает новый экземпляр LRU-кэша с указанным максимальным размером.
     *
     * @param maxSize максимальное количество элементов в кэше
     * @throws IllegalArgumentException если maxSize меньше или равен 0
     */
    public CacheUtil(final int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Размер кэша должен быть положительным числом");
        }
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    LOGGER.info("Удалена старая запись (достигнут лимит элементов)");
                }
                return shouldRemove;
            }
        };
    }

    /**
     * Получает значение по ключу из кэша.
     *
     * @param key ключ для поиска
     * @return значение, соответствующее ключу, или null если ключ отсутствует
     */
    public V get(final K key) {
        lock.lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                LOGGER.info("Получено из кэша по ключу: ");
            } else {
                LOGGER.info("Данные не найдены в кэше по ключу:");
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Помещает пару ключ-значение в кэш.
     *
     * @param key ключ
     * @param value значение
     * @throws IllegalArgumentException если ключ или значение null
     */
    public void put(final K key, final V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Ключ и значение не могут быть null");
        }
        lock.lock();
        try {
            cache.put(key, value);
            LOGGER.info("Сохранено в кэш.");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Возвращает текущий размер кэша.
     *
     * @return количество элементов в кэше
     */
    public int size() {
        lock.lock();
        try {
            return cache.size();
        } finally {
            lock.unlock();
        }
    }
}