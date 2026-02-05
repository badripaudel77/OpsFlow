package com.miu.flowops.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.miu.flowops.dto.StaleTaskDetectedEvent;
import com.miu.flowops.service.impl.EmailService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.miu.flowops.dto.CriticalSystemErrorEvent;
import com.miu.flowops.dto.HotfixTaskAddedEvent;
import com.miu.flowops.dto.TaskAssignedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestEventController {

    private static final String TOPIC_TASK_ASSIGNED = "task-assigned-topic";
    private static final String TOPIC_HOTFIX_ADDED = "hotfix-task-added-topic";
    private static final String TOPIC_STALE_REMINDER = "stale-task-reminder-topic";
    private static final String TOPIC_CRITICAL_ERROR = "critical-system-error-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final EmailService emailService;

    @GetMapping
    public String sendEmail() {
        emailService.sendEmail("badripaudel77@gmail.com", "OpsFlow", "This task has been assigned to you");
        return "email sent";
    }
    @PostMapping("/task-assigned")
    public String triggerTaskAssigned(@RequestBody(required = false) TaskAssignedEvent event) {
        TaskAssignedEvent toSend = event != null ? event : TaskAssignedEvent.builder()
                .developerId("dev-test-123")
                .releaseId("rel-test-456")
                .taskId("task-test-789")
                .taskTitle("Fix login bug")
                .email("developer@example.com")
                .message("Task assigned to you")
                .build();
        kafkaTemplate.send(TOPIC_TASK_ASSIGNED, toSend);
        log.info("Sent test TaskAssignedEvent to Kafka");
        return "Task assigned event sent. Check logs and MongoDB.";
    }

    @PostMapping("/hotfix-added")
    public String triggerHotfixAdded(@RequestBody(required = false) HotfixTaskAddedEvent event) {
        HotfixTaskAddedEvent toSend = event != null ? event : HotfixTaskAddedEvent.builder()
                .developerId("dev-test-123")
                .releaseId("rel-test-456")
                .taskTitle("Urgent: Fix production bug")
                .email("developer@example.com")
                .message("A hotfix task has been added to the release. Please review.")
                .build();
        kafkaTemplate.send(TOPIC_HOTFIX_ADDED, toSend);
        log.info("Sent test HotfixTaskAddedEvent to Kafka");
        return "Hotfix added event sent. Check logs and MongoDB.";
    }

    @PostMapping("/stale-reminder")
    public String triggerStaleReminder(@RequestBody(required = false) StaleTaskDetectedEvent event) {
        StaleTaskDetectedEvent toSend = event != null ? event : StaleTaskDetectedEvent.builder()
                .developerId("dev-test-123")
                .taskId("task-test-789")
                .taskTitle("Test Stale Task")
                .email("test@example.com")
                .message("This task has been in progress for over 48 hours. Please complete or update.")
                .build();
        kafkaTemplate.send(TOPIC_STALE_REMINDER, toSend);
        log.info("Sent test StaleTaskReminderEvent to Kafka");
        return "Stale reminder event sent. Check logs and MongoDB.";
    }

    @PostMapping("/critical-error")
    public String triggerCriticalError(@RequestBody(required = false) CriticalSystemErrorEvent event) {
        CriticalSystemErrorEvent toSend = event != null ? event : CriticalSystemErrorEvent.builder()
                .serviceName("release-service")
                .errorMessage("Database connection pool exhausted")
                .severity("CRITICAL")
                .occurredAt(LocalDateTime.now())
                .recipientEmails(List.of("admin@example.com", "ops@example.com"))
                .build();
        kafkaTemplate.send(TOPIC_CRITICAL_ERROR, toSend);
        log.info("Sent test CriticalSystemErrorEvent to Kafka");
        return "Critical error event sent. Check logs and MongoDB.";
    }
}