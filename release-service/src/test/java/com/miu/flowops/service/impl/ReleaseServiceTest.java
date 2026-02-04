package com.miu.flowops.service.impl;

import com.miu.flowops.dto.TaskAssignedEvent;
import com.miu.flowops.exceptions.ResourceNotFoundException;
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
class ReleaseServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ReleaseService releaseService;

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
    void createRelease_ShouldSaveRelease() {
        when(releaseRepository.save(any(Release.class))).thenReturn(release);

        Release createdRelease = releaseService.createRelease(release);

        assertNotNull(createdRelease);
        assertEquals(release.getId(), createdRelease.getId());
        verify(releaseRepository, times(1)).save(any(Release.class));
    }

    @Test
    void getRelease_ShouldReturnRelease_WhenFound() {
        when(releaseRepository.findById("release-1")).thenReturn(Optional.of(release));

        Release foundRelease = releaseService.getRelease("release-1");

        assertNotNull(foundRelease);
        assertEquals("release-1", foundRelease.getId());
    }

    @Test
    void getRelease_ShouldThrowException_WhenNotFound() {
        when(releaseRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> releaseService.getRelease("invalid-id"));
    }

    @Test
    void assignDeveloper_ShouldAssignDeveloperAndPublishEvent() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(releaseRepository.findById("release-1")).thenReturn(Optional.of(release));
        when(releaseRepository.save(any(Release.class))).thenReturn(release);

        Task assignedTask = releaseService.assignDeveloper("release-1", "task-1", "user-1");

        assertNotNull(assignedTask);
        assertEquals("user-1", assignedTask.getDeveloperId());
        verify(kafkaProducerService, times(1)).sendTaskAssignedEvent(any(TaskAssignedEvent.class));
        verify(releaseRepository, times(1)).save(any(Release.class));
    }

    @Test
    void addHotfixTask_ShouldAddTaskAndReopenRelease() {
        release.setIsCompleted(true);
        Task hotfixTask = Task.builder().title("Hotfix").build();

        when(releaseRepository.findById("release-1")).thenReturn(Optional.of(release));
        when(releaseRepository.save(any(Release.class))).thenReturn(release);

        releaseService.addHotfixTask("release-1", hotfixTask);

        assertFalse(release.getIsCompleted());
        assertEquals(2, release.getTasks().size());
        verify(releaseRepository, times(1)).save(any(Release.class));
    }

    @Test
    void completeRelease_ShouldComplete_WhenAllTasksDone() {
        task.setStatus(TaskStatus.COMPLETED);
        when(releaseRepository.findById("release-1")).thenReturn(Optional.of(release));
        when(releaseRepository.save(any(Release.class))).thenReturn(release);

        Release completedRelease = releaseService.completeRelease("release-1");

        assertTrue(completedRelease.getIsCompleted());
        verify(releaseRepository, times(1)).save(any(Release.class));
    }

    @Test
    void completeRelease_ShouldThrowException_WhenTasksNotDone() {
        task.setStatus(TaskStatus.IN_PROCESS);
        when(releaseRepository.findById("release-1")).thenReturn(Optional.of(release));

        assertThrows(IllegalStateException.class, () -> releaseService.completeRelease("release-1"));
    }

    @Test
    void deleteRelease_ShouldDelete_WhenExists() {
        when(releaseRepository.existsById("release-1")).thenReturn(true);

        releaseService.deleteRelease("release-1");

        verify(releaseRepository, times(1)).deleteById("release-1");
    }

    @Test
    void deleteRelease_ShouldThrowException_WhenNotExists() {
        when(releaseRepository.existsById("invalid-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> releaseService.deleteRelease("invalid-id"));
    }
}
