package com.miu.flowops.service;

import com.miu.flowops.dto.HotfixTaskAddedEvent;
import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.dto.TaskCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_TASK_ASSIGNED = "task-assigned-topic";
    private static final String TOPIC_TASK_COMPLETED = "task-completed-topic";
    private static final String TOPIC_HOTFIX_ADDED = "hotfix-task-added-topic";

    public void sendTaskAssignedEvent(TaskAssignedEvent event) {
        log.info("Publishing TaskAssignedEvent: {}", event);
        kafkaTemplate.send(TOPIC_TASK_ASSIGNED, event);
    }

    public void sendTaskCompletedEvent(TaskCompletedEvent event) {
        log.info("Publishing TaskCompletedEvent: {}", event);
        kafkaTemplate.send(TOPIC_TASK_COMPLETED, event);
    }

    public void sendHotfixTaskAddedEvent(HotfixTaskAddedEvent event) {
        log.info("Publishing HotfixTaskAddedEvent: {}", event);
        kafkaTemplate.send(TOPIC_HOTFIX_ADDED, event);
    }
}
