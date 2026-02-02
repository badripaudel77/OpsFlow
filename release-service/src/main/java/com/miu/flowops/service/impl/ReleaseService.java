package com.miu.flowops.service.impl;

import com.miu.flowops.dto.HotfixTaskAddedEvent;
import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.ReleaseRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.IReleaseService;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReleaseService implements IReleaseService {

    private final ReleaseRepository releaseRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Release createRelease(Release release) {
        if (release.getTasks() != null && !release.getTasks().isEmpty()) {
            int order = 1;
            for (Task task : release.getTasks()) {
                // Assign ID if missing
                if (task.getId() == null) {
                    task.setId(UUID.randomUUID().toString());
                }
                // Auto-assign orderIndex
                task.setOrderIndex(order++);
                if (task.getStatus() == null) {
                    task.setStatus(TaskStatus.TODO);
                }
            }
            // sort by orderIndex
            release.getTasks().sort(Comparator.comparing(Task::getOrderIndex));
        }
        // Set release completed flag
        release.setIsCompleted(false);
        return releaseRepository.save(release);
    }

    @Override
    public Release getRelease(String id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Release not found"));
    }

    @Override
    public Task assignDeveloper(String releaseId, String taskId, String developerId) {
        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found"));
        Release release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found"));
        Task task = release.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setDeveloperId(developer.getId());

        // publish TaskAssignedEvent to Kafka here
        kafkaProducerService.sendTaskAssignedEvent(TaskAssignedEvent.builder()
                .developerId(developerId)
                .email(developer.getEmail())
                .releaseId(releaseId)
                .taskId(taskId)
                .message("Task assigned to you")
                .taskTitle(task.getTitle())
                .build());

        Release savedRelease = releaseRepository.save(release);
        return savedRelease.getTasks()
                .stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Task Not found"));
    }

    @Override
    public void addHotfixTask(String releaseId, Task newTask) {
        Release release = getRelease(releaseId);
        if (!release.getIsCompleted()) {
            // Depending on requirements, maybe allow adding tasks to active releases too,
            // We will allow it but the specific logic for reopening applies if it was completed.
        }
        // Reopen Release if completed
        if (release.getIsCompleted()) {
            release.setIsCompleted(false);
        }
        // Add Task
        if (newTask.getId() == null) {
            newTask.setId(UUID.randomUUID().toString());
        }
        newTask.setStatus(TaskStatus.TODO);
        // Determine order index (append to end)
        int maxOrder = release.getTasks().stream()
                .mapToInt(Task::getOrderIndex)
                .max()
                .orElse(0);

        newTask.setOrderIndex(maxOrder + 1);
        release.getTasks().add(newTask);
        releaseRepository.save(release);

        // Publish Event
        if (newTask.getDeveloperId() != null) {
            kafkaProducerService.sendHotfixTaskAddedEvent(HotfixTaskAddedEvent.builder()
                    .developerId(newTask.getDeveloperId())
                    .releaseId(releaseId)
                    .taskTitle(newTask.getTitle())
                    .build());
        }
    }

    protected Task findTask(Release release, String taskId) {
        return release.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Task not found in release"));
    }

    protected void validateSequentialExecution(Release release, Task currentTask) {
        // Sort tasks by order index
        List<Task> sortedTasks = release.getTasks().stream()
                .sorted(Comparator.comparingInt(Task::getOrderIndex))
                .toList();

        int currentIndex = sortedTasks.indexOf(currentTask);
        if (currentIndex > 0) {
            Task previousTask = sortedTasks.get(currentIndex - 1);
            if (previousTask.getStatus() != TaskStatus.COMPLETED) {
                throw new RuntimeException("Previous task '" + previousTask.getTitle() + "' is not completed.");
            }
        }
    }

    protected void checkAndCompleteRelease(Release release) {
        boolean allCompleted = release.getTasks().stream()
                .allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);

        if (allCompleted) {
            release.setIsCompleted(true);
        }
    }
}
