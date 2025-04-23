package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.LogTask;
import com.example.demo.model.LogTaskStatus;
import com.example.demo.repository.LogTaskRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing log files asynchronously.
 */
@Slf4j
@Service
public class LogService {
    private static final String LOG_FILE_PATH = "application.log";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LogTaskRepository taskRepository;
    private final AtomicLong idCounter = new AtomicLong(1);
    private final LogService selfProxy;

    /**
     * Constructs a LogService with required dependencies.
     *
     * @param taskRepository repository for log tasks
     */
    public LogService(LogTaskRepository taskRepository, @Lazy LogService selfProxy) {
        this.taskRepository = taskRepository;
        this.selfProxy = selfProxy;
    }

    /**
     * Creates a new log processing task.
     *
     * @param date the date to filter logs in format yyyy-MM-dd
     * @return the created task ID
     * @throws IllegalArgumentException if date format is invalid
     */
    @Transactional
    public String createLogTask(String date) {
        validateDate(date);
        String taskId = String.valueOf(idCounter.getAndIncrement());
        LogTask task = new LogTask(taskId, date, LogTaskStatus.PROCESSING);
        taskRepository.save(task);

        // Call async method through proxy
        selfProxy.processTask(task);

        return taskId;
    }

    /**
     * Asynchronously processes the task of filtering and saving logs for the specified date.
     *
     */
    @Async("logTaskExecutor")
    @Transactional
    public void processTask(LogTask task) {
        try {
            // Simulate long processing
            Thread.sleep(20000);

            Path logFile = Paths.get(LOG_FILE_PATH);
            List<String> filteredLines = Files.lines(logFile)
                    .filter(line -> line.contains(task.getDate()))
                    .toList();

            Path tempFile = createTempFile(task.getDate(), filteredLines);

            // Call transactional method through proxy
            selfProxy.updateTaskStatus(
                    task,
                    LogTaskStatus.COMPLETED,
                    tempFile.toString()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            selfProxy.updateTaskStatus(
                    task,
                    LogTaskStatus.FAILED,
                    "Processing interrupted"
            );
            log.warn("Task {} was interrupted", task.getId());

        } catch (Exception e) {
            selfProxy.updateTaskStatus(
                    task,
                    LogTaskStatus.FAILED,
                    e.getMessage()
            );
            log.error("Task failed: {}", task.getId(), e);
        }
    }

    /**
     * Updates the status of a log task and optionally sets an error message.
     */
    @Transactional
    public void updateTaskStatus(LogTask task, LogTaskStatus status,
                                 String filePathOrErrorMessage) {
        Objects.requireNonNull(task, "Task must not be null");
        Objects.requireNonNull(status, "Status must not be null");

        task.setStatus(status);
        if (status == LogTaskStatus.COMPLETED) {
            task.setFilePath(filePathOrErrorMessage);
        } else {
            task.setErrorMessage(filePathOrErrorMessage);
        }
        taskRepository.save(task);
    }

    /**
     * Gets the status of a task.
     *
     * @param taskId the ID of the task to check
     * @return the log task with current status
     * @throws ResourceNotFoundException if task not found
     */
    @Transactional(readOnly = true)
    public LogTask getTaskStatus(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    /**
     * Gets the path to processed log file.
     *
     * @param taskId the ID of completed task
     * @return path to the log file
     * @throws IllegalStateException if file not ready
     * @throws ResourceNotFoundException if file path not found
     */
    @Transactional(readOnly = true)
    public Path getLogFilePath(String taskId) {
        LogTask task = selfProxy.getTaskStatus(taskId);
        if (task.getStatus() != LogTaskStatus.COMPLETED) {
            throw new IllegalStateException("Log file not ready yet"); // Это вызовет 425
        }
        if (task.getFilePath() == null) {
            throw new ResourceNotFoundException("Log file path not found");
        }
        return Paths.get(task.getFilePath());
    }

    private Path createTempFile(String date, List<String> content) throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path tempFile = Files.createTempFile(tempDir, "logs-" + date + "-", ".log");

        try {
            setFilePermissions(tempFile);
            Files.write(tempFile, content, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            Files.deleteIfExists(tempFile);
            throw e;
        }
    }

    private void setFilePermissions(Path file) throws IOException {
        try {
            Files.setPosixFilePermissions(file, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            ));
        } catch (UnsupportedOperationException e) {
            if (!file.toFile().setReadable(true) || !file.toFile().setWritable(true)) {
                log.warn("Failed to set file permissions on: {}", file);
            }
        }
    }

    private void validateDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
    }
}