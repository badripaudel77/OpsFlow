package com.miu.flowops.service;

import java.time.LocalDateTime;

import com.miu.flowops.dto.CriticalSystemErrorEvent;
import com.miu.flowops.dto.StaleTaskDetectedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.miu.flowops.dto.HotfixTaskAddedEvent;
import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.model.NotificationLog;
import com.miu.flowops.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private static final String TOPIC_TASK_ASSIGNED = "task-assigned-topic";
    private static final String TOPIC_TASK_COMPLETED = "task-completed-topic";
    private static final String TOPIC_HOTFIX_ADDED = "hotfix-task-added-topic";
    private static final String TOPIC_STALE_TASK_REMINDER = "stale-task-reminder-topic";
    private static final String TOPIC_CRITICAL_SYSTEM_ERROR = "critical-system-error-topic";


    private final IEmailService emailService;
    private final NotificationLogRepository notificationLogRepository;
    private final ObjectMapper objectMapper;

    // listen ..... and send email
    @KafkaListener(topics = TOPIC_TASK_ASSIGNED, groupId = "task-assigned-group")
    public void listenTaskAssigned(TaskAssignedEvent event) {
        log.info("Received TaskAssignedEvent: {}", event);

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            payloadJson = "serialization failed: " + e.getMessage();
        }

        NotificationLog logEntry = NotificationLog.builder()
                .notificationType("TASK_ASSIGNED")
                .recipientEmail(event.getEmail())
                .subject("Task Assigned")
                .message(event.getMessage())
                .sentAt(LocalDateTime.now())
                .status("PENDING")
                .eventPayload(payloadJson)
                .build();

        try {
            emailService.sendEmail(event.getEmail(), "Task Assigned", event.getMessage(), event.getReleaseId(), event.getTaskId(), event.getDeveloperId());
            logEntry.setStatus("SENT");
        } catch (Exception e) {
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
        }
        notificationLogRepository.save(logEntry);
    }

    @KafkaListener(topics = TOPIC_HOTFIX_ADDED, groupId = "hotfix-added-group")
    public void listenHotfixAdded(HotfixTaskAddedEvent event) {
        log.info("Received HotfixTaskAddedEvent: {}", event);

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            payloadJson = "serialization failed: " + e.getMessage();
        }
        NotificationLog logEntry = NotificationLog.builder()
                .notificationType("HOTFIX_ADDED")
                .recipientEmail(event.getEmail())
                .subject("Hotfix Added")
                .message(event.getMessage())
                .sentAt(LocalDateTime.now())
                .status("PENDING")
                .eventPayload(payloadJson)
                .build();

        try {
            emailService.sendEmail(event.getEmail(), "Hotfix Task Added", event.getMessage(), event.getReleaseId(), null, event.getDeveloperId());
            logEntry.setStatus("SENT");
        } catch (Exception e) {
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
        }
        notificationLogRepository.save(logEntry);
    }

    @KafkaListener(topics = TOPIC_STALE_TASK_REMINDER, groupId = "stale-task-reminder-group")
    public void listenStaleTaskReminder(StaleTaskDetectedEvent event) {
        log.info("Received StaleTaskDetectedEvent: {}", event);

        NotificationLog logEntry = null;
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
            logEntry = NotificationLog.builder()
                    .notificationType("STALE_TASK_REMINDER")
                    .recipientEmail(event.getEmail())
                    .subject("Stale Task Reminder")
                    .message(event.getMessage())
                    .sentAt(LocalDateTime.now())
                    .status("PENDING")
                    .build();

            emailService.sendEmail(event.getEmail(), "Stale Task Reminder", event.getMessage(), null, event.getTaskId(), event.getDeveloperId());
            logEntry.setStatus("SENT");
        }
        catch (JacksonException e) {
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
            payloadJson = "serialization failed: " + e.getMessage();
            logEntry.setEventPayload(payloadJson);
        }
        notificationLogRepository.save(logEntry);
    }

    @KafkaListener(topics = TOPIC_CRITICAL_SYSTEM_ERROR, groupId = "critical-system-error-group")
    public void listenCriticalSystemError(CriticalSystemErrorEvent event) {
        log.info("Received CriticalSystemErrorEvent: {}", event);

        try {
            String payloadJson;
            payloadJson = objectMapper.writeValueAsString(event);
            NotificationLog logEntry;
            for (String email : event.getRecipientEmails()) {
                logEntry = NotificationLog.builder()
                        .notificationType("CRITICAL_SYSTEM_ERROR")
                        .recipientEmail(email)
                        .subject("Critical System Error")
                        .message(event.getErrorMessage())
                        .sentAt(LocalDateTime.now())
                        .status("PENDING")
                        .eventPayload(payloadJson)
                        .build();
                emailService.sendEmail(email, "Critical System Error", event.getErrorMessage());
                logEntry.setStatus("SENT");
                notificationLogRepository.save(logEntry);
            }
        }
        catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }
}
