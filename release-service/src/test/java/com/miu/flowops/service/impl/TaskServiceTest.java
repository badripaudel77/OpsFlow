package com.miu.flowops.service.impl;

import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.dto.TaskCompletedEvent;
import com.miu.flowops.exceptions.BadRequestException;
import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.ReleaseRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private TaskService taskService;

    private Release release;
    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id("task-1")
                .title("Test Task")
                .status(TaskStatus.TODO)
                .orderIndex(1)
                .build();

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        release = Release.builder()
                .id("release-1")
                .title("Release 1.0")
                .isCompleted(false)
                .tasks(tasks)
                .build();

        user = User.builder()
                .id("user-1")
                .email("test@example.com")
                .build();
    }

    @Test
    void startTask_ShouldStartTask_WhenValid() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(releaseRepository.existsByDeveloperIdAndTaskStatus("user-1", TaskStatus.IN_PROCESS)).thenReturn(false);
        when(releaseService.getRelease("release-1")).thenReturn(release);
        when(releaseService.findTask(release, "task-1")).thenReturn(task);

        taskService.startTask("release-1", "task-1", "user-1");

        assertEquals(TaskStatus.IN_PROCESS, task.getStatus());
        assertEquals("user-1", task.getDeveloperId());
        verify(releaseRepository, times(1)).save(release);
        verify(kafkaProducerService, times(1)).sendTaskAssignedEvent(any(TaskAssignedEvent.class));
    }

    @Test
    void startTask_ShouldThrowException_WhenDeveloperHasActiveTask() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(releaseRepository.existsByDeveloperIdAndTaskStatus("user-1", TaskStatus.IN_PROCESS)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> taskService.startTask("release-1", "task-1", "user-1"));
    }

    @Test
    void startTask_ShouldThrowException_WhenTaskNotTodo() {
        task.setStatus(TaskStatus.IN_PROCESS);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(releaseRepository.existsByDeveloperIdAndTaskStatus("user-1", TaskStatus.IN_PROCESS)).thenReturn(false);
        when(releaseService.getRelease("release-1")).thenReturn(release);
        when(releaseService.findTask(release, "task-1")).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskService.startTask("release-1", "task-1", "user-1"));
    }

    @Test
    void completeTask_ShouldCompleteTask_WhenValid() {
        task.setStatus(TaskStatus.IN_PROCESS);
        task.setDeveloperId("user-1");

        when(releaseService.getRelease("release-1")).thenReturn(release);
        when(releaseService.findTask(release, "task-1")).thenReturn(task);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        taskService.completeTask("release-1", "task-1", "user-1");

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        verify(releaseRepository, times(1)).save(release);
        verify(kafkaProducerService, times(1)).sendTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void completeTask_ShouldThrowException_WhenTaskNotProcessing() {
        task.setStatus(TaskStatus.TODO);
        when(releaseService.getRelease("release-1")).thenReturn(release);
        when(releaseService.findTask(release, "task-1")).thenReturn(task);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> taskService.completeTask("release-1", "task-1", "user-1"));
    }

    @Test
    void completeTask_ShouldThrowException_WhenDifferentDeveloper() {
        task.setStatus(TaskStatus.IN_PROCESS);
        task.setDeveloperId("other-user");

        when(releaseService.getRelease("release-1")).thenReturn(release);
        when(releaseService.findTask(release, "task-1")).thenReturn(task);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> taskService.completeTask("release-1", "task-1", "user-1"));
    }
}
