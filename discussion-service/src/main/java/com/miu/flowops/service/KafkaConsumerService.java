package com.miu.flowops.service;

import com.miu.flowops.dto.CreateDiscussionRequest;
import com.miu.flowops.model.DiscussionType;
import com.miu.flowops.service.impl.DiscussionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final DiscussionService discussionService;

    @KafkaListener(topics = "task-assigned-topic", groupId = "discussion-event-group")
    public void handleTaskAssignedEvent(Map<String, Object> event) {
        log.info("Received TaskAssignedEvent: {}", event);
        // Optionally create an automatic discussion for task assignment
        // This is optional - discussions can be created manually by users
    }

    @KafkaListener(topics = "hotfix-task-added-topic", groupId = "discussion-event-group")
    public void handleHotfixTaskAddedEvent(Map<String, Object> event) {
        log.info("Received HotfixTaskAddedEvent: {}", event);
        
        // Automatically create a discussion for hotfix tasks
        try {
            String releaseId = (String) event.get("releaseId");
            String taskTitle = (String) event.get("taskTitle");
            String developerId = (String) event.get("developerId");

            if (developerId != null && releaseId != null) {
                CreateDiscussionRequest request = CreateDiscussionRequest.builder()
                        .title("Hotfix Task: " + taskTitle)
                        .content("A new hotfix task has been added to the release. Please discuss the implementation approach and any concerns.")
                        .authorId(developerId)
                        .releaseId(releaseId)
                        .type(DiscussionType.ISSUE)
                        .build();

                discussionService.createDiscussion(request);
                log.info("Auto-created discussion for hotfix task: {}", taskTitle);
            }
        } catch (Exception e) {
            log.error("Error creating discussion for hotfix task: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "task-completed-topic", groupId = "discussion-event-group")
    public void handleTaskCompletedEvent(Map<String, Object> event) {
        log.info("Received TaskCompletedEvent: {}", event);
        // Log task completion - discussions related to this task can be resolved manually
    }
}
