package com.example.demo.service;

import com.example.demo.model.LogTask;
import com.example.demo.model.LogTaskStatus;
import com.example.demo.repository.LogTaskRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для обработки задач работы с логами.
 */
@Slf4j
@Service
public class LogService {
    private static final String LOG_FILE_PATH = "application.log";
    private final LogTaskRepository taskRepository;
    private final AtomicLong idCounter = new AtomicLong(1);
    private final LogService selfProxy;

    /**
     * Конструктор сервиса для обработки задач работы с логами.
     *
     * @param taskRepository репозиторий для работы с задачами обработки логов
     * @param selfProxy лениво инициализируемый прокси текущего сервиса,
     *                 необходим для корректной работы транзакционных методов
     *                 при внутренних вызовах
     */
    @org.springframework.beans.factory.annotation.Autowired
    public LogService(LogTaskRepository taskRepository, @Lazy LogService selfProxy) {
        this.taskRepository = taskRepository;
        this.selfProxy = selfProxy;
    }

    /**
     * Создает новую задачу для обработки логов за указанную дату.
     *
     * @param date Дата в формате yyyy-MM-dd для фильтрации логов
     * @return CompletableFuture с созданной задачей
     */
    @Async
    @Transactional
    public CompletableFuture<LogTask> createLogTask(String date) {
        LogTask task = new LogTask(
                String.valueOf(idCounter.getAndIncrement()),
                date
        );
        taskRepository.save(task);
        return CompletableFuture.supplyAsync(() -> processTask(task));
    }

    /**
     * Обрабатывает задачу: фильтрует логи по дате и сохраняет результат.
     *
     * @param task Задача для обработки
     * @return Обработанная задача с обновленным статусом
     */
    private LogTask processTask(LogTask task) {
        try {
            task.setStatus(LogTaskStatus.PROCESSING);
            taskRepository.save(task);
            Thread.sleep(10000);
            Path logFile = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(logFile)) {
                throw new IOException("Log file not found");
            }

            List<String> filteredLines = filterLinesByDate(logFile, task.getDate());
            Path tempFile = createTempFile(task.getDate(), filteredLines);

            task.setStatus(LogTaskStatus.COMPLETED);
            task.setFilePath(tempFile.toString());
            return taskRepository.save(task);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Task processing interrupted: {}", task.getId(), e);
            task.setStatus(LogTaskStatus.FAILED);
            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Task processing failed: {}", task.getId(), e);
            task.setStatus(LogTaskStatus.FAILED);
            return taskRepository.save(task);
        }
    }

    /**
     * Фильтрует строки лога, оставляя только записи за указанную дату.
     *
     * @param logFile Путь к файлу с логами
     * @param date Дата для фильтрации
     * @return Список отфильтрованных строк
     * @throws IOException если возникла ошибка чтения файла
     */
    private List<String> filterLinesByDate(Path logFile, String date) throws IOException {
        return Files.lines(logFile)
                .filter(line -> line.contains(date))
                .toList();
    }

    /**
     * Создает временный файл с отфильтрованными логами.
     *
     * @param date Дата для включения в имя файла
     * @param content Содержимое файла
     * @return Путь к созданному временному файлу
     * @throws IOException если возникла ошибка создания файла
     */
    private Path createTempFile(String date, List<String> content) throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        if (!Files.isWritable(tempDir)) {
            throw new SecurityException("Temporary directory is not writable: " + tempDir);
        }

        Path tempFile = Files.createTempFile(tempDir, "logs-" + date + "-", ".log");
        try {
            setFilePermissions(tempFile);
            writeFileContent(tempFile, content);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            cleanupTempFile(tempFile);
            throw e;
        }
    }

    /**
     * Устанавливает права доступа для файла.
     */
    private void setFilePermissions(Path file) throws IOException {
        try {
            Files.setPosixFilePermissions(file, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            ));
        } catch (UnsupportedOperationException e) {
            if (!file.toFile().setReadable(false, false)
                    || !file.toFile().setWritable(false, false)
                    || !file.toFile().setReadable(true)
                    || !file.toFile().setWritable(true)) {
                log.warn("Failed to set file permissions on: {}", file);
            }
        }
    }

    /**
     * Записывает содержимое в файл.
     */
    private void writeFileContent(Path file, List<String> content) throws IOException {
        Files.write(file, content,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Удаляет временный файл в случае ошибки.
     */
    private void cleanupTempFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete temporary file: {}", file, e);
        }
    }

    /**
     * Возвращает статус задачи по её идентификатору.
     *
     * @param taskId Идентификатор задачи
     * @return Объект задачи
     * @throws IllegalArgumentException если задача не найдена
     */
    @Transactional(readOnly = true)
    public LogTask getTaskStatus(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found: {}", taskId);
                    return new IllegalArgumentException("Task not found");
                });
    }

    /**
     * Возвращает путь к готовому файлу логов.
     *
     * @param taskId Идентификатор задачи
     * @return Путь к файлу с логами
     * @throws IllegalStateException если файл еще не готов
     */
    @Transactional(readOnly = true)
    public Path getLogFilePath(String taskId) {
        LogTask task = selfProxy.getTaskStatus(taskId);
        if (task.getStatus() != LogTaskStatus.COMPLETED) {
            throw new IllegalStateException("Log file not ready yet");
        }
        return Paths.get(task.getFilePath());
    }
}