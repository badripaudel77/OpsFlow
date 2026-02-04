package com.miu.flowops.service.impl;

import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.dto.TaskCompletedEvent;
import com.miu.flowops.exceptions.BadRequestException;
import com.miu.flowops.exceptions.ResourceNotFoundException;
import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.ReleaseRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.ITaskService;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final UserRepository userRepository;
    private final ReleaseRepository releaseRepository;
    private final ReleaseService releaseService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void startTask(String releaseId, String taskId, String developerId) {
        // Check Global Constraint: Developer cannot have more than one IN_PROCESS task
        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found"));

        boolean hasActiveTask = releaseRepository.existsByDeveloperIdAndTaskStatus(developerId, TaskStatus.IN_PROCESS);
        if (hasActiveTask) {
            throw new BadRequestException("Developer already has an active task in process.");
        }

        Release release = releaseService.getRelease(releaseId);
        Task task = releaseService.findTask(release, taskId);

        // Validate Task State
        if (task.getStatus() != TaskStatus.TODO) {
            throw new BadRequestException("Task must be in TODO state to start.");
        }
        // Sequential Execution Enforcement
        releaseService.validateSequentialExecution(release, task);

        // Update Task
        task.setStatus(TaskStatus.IN_PROCESS);
        task.setDeveloperId(developer.getId());
        task.setStartedAt(LocalDateTime.now());

        releaseRepository.save(release);

        // Publish Event
        kafkaProducerService.sendTaskAssignedEvent(TaskAssignedEvent.builder()
                .developerId(developer.getId())
                .email(developer.getEmail())
                .releaseId(releaseId)
                .taskId(taskId)
                .message("Developer started task")
                .taskTitle(task.getTitle())
                .build());
    }

    @Override
    public void completeTask(String releaseId, String taskId, String developerId) {
        Release release = releaseService.getRelease(releaseId);
        Task task = releaseService.findTask(release, taskId);

        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found"));

        // Validate Task State
        if (task.getStatus() != TaskStatus.IN_PROCESS) {
            throw new BadRequestException("Task must be IN_PROCESS to complete.");
        }
        // Verify developer
        if (task.getDeveloperId() != null && !task.getDeveloperId().equals(developer.getId())) {
            throw new BadRequestException("Task is assigned to a different developer.");
        }
        // Update Task
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());

        // Check Release Completion
        releaseService.checkAndCompleteRelease(release);
        releaseRepository.save(release);

        // Publish Event
        kafkaProducerService.sendTaskCompletedEvent(TaskCompletedEvent.builder()
                .developerId(developerId)
                .email(developer.getEmail())
                .releaseId(releaseId)
                .taskId(taskId)
                .message("Developer completed task")
                .taskTitle(task.getTitle())
                .build());
    }

}
