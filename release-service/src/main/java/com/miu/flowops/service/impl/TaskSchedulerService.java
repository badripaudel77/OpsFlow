package com.miu.flowops.service.impl;

import com.miu.flowops.dto.StaleTaskDetectedEvent;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.ReleaseRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final ReleaseRepository releaseRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void checkStaleTasks() {
        log.info("Running scheduler to detect stale tasks with IN_PROCESS status for more than 48 hours");
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        List<Task> matchingTasks = releaseRepository.findTasksByStatusAndStartedBefore(TaskStatus.IN_PROCESS, threshold);
        log.info("Found {} stale tasks with IN_PROCESS tasks", matchingTasks.size());
        for (Task task : matchingTasks) {
            if (task != null) {
                handleStaleTask(task);
            }
        }
    }

    private void handleStaleTask(Task task) {
        String developerId = task.getDeveloperId();
        String email = "no-reply@opsflow.com";
        if (developerId != null) {
            User developer = userRepository.findById(developerId).orElse(null);
            if (developer != null) {
                email = developer.getEmail();
            }
        }
        StaleTaskDetectedEvent event = StaleTaskDetectedEvent.builder()
                .developerId(developerId)
                .email(email)
                .taskId(task.getId())
                .taskTitle(task.getTitle())
                .message("Task has been in process for more than 48 hours.")
                .build();

        kafkaProducerService.sendStaleTaskDetectedEvent(event);
    }
}
