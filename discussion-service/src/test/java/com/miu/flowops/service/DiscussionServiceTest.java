package com.miu.flowops.service;

import com.miu.flowops.dto.CreateDiscussionRequest;
import com.miu.flowops.dto.UpdateDiscussionRequest;
import com.miu.flowops.model.*;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.impl.DiscussionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private DiscussionService discussionService;

    private User testUser;
    private Discussion testDiscussion;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .username("testuser")
                .fullName("Test User")
                .roles(Set.of(Role.DEVELOPER))
                .build();

        testDiscussion = Discussion.builder()
                .id("discussion-123")
                .title("Test Discussion")
                .content("Test content")
                .authorId("user-123")
                .authorName("Test User")
                .releaseId("release-123")
                .taskId("task-123")
                .type(DiscussionType.GENERAL)
                .status(DiscussionStatus.OPEN)
                .comments(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createDiscussion_Success() {
        CreateDiscussionRequest request = CreateDiscussionRequest.builder()
                .title("Test Discussion")
                .content("Test content")
                .authorId("user-123")
                .releaseId("release-123")
                .type(DiscussionType.GENERAL)
                .build();

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        Discussion result = discussionService.createDiscussion(request);

        assertNotNull(result);
        assertEquals("Test Discussion", result.getTitle());
        assertEquals(DiscussionStatus.OPEN, result.getStatus());
        verify(kafkaProducerService).sendDiscussionCreatedEvent(any());
    }

    @Test
    void createDiscussion_UserNotFound_ThrowsException() {
        CreateDiscussionRequest request = CreateDiscussionRequest.builder()
                .title("Test Discussion")
                .authorId("unknown-user")
                .build();

        when(userRepository.findById("unknown-user")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> discussionService.createDiscussion(request));
    }

    @Test
    void getDiscussion_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        Discussion result = discussionService.getDiscussion("discussion-123");

        assertNotNull(result);
        assertEquals("discussion-123", result.getId());
    }

    @Test
    void getDiscussion_NotFound_ThrowsException() {
        when(discussionRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> discussionService.getDiscussion("unknown"));
    }

    @Test
    void updateDiscussion_Success() {
        UpdateDiscussionRequest request = UpdateDiscussionRequest.builder()
                .title("Updated Title")
                .content("Updated content")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        Discussion result = discussionService.updateDiscussion("discussion-123", request, "user-123");

        assertNotNull(result);
        verify(discussionRepository).save(any(Discussion.class));
    }

    @Test
    void updateDiscussion_NotAuthor_ThrowsException() {
        UpdateDiscussionRequest request = UpdateDiscussionRequest.builder()
                .title("Updated Title")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        assertThrows(RuntimeException.class, () -> 
            discussionService.updateDiscussion("discussion-123", request, "different-user"));
    }

    @Test
    void resolveDiscussion_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        Discussion result = discussionService.resolveDiscussion("discussion-123", "user-123");

        assertNotNull(result);
        verify(kafkaProducerService).sendDiscussionResolvedEvent(any());
    }

    @Test
    void resolveDiscussion_AlreadyResolved_ThrowsException() {
        testDiscussion.setStatus(DiscussionStatus.RESOLVED);

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> 
            discussionService.resolveDiscussion("discussion-123", "user-123"));
    }

    @Test
    void getDiscussionsByRelease_Success() {
        List<Discussion> discussions = List.of(testDiscussion);
        when(discussionRepository.findByReleaseIdOrderByCreatedAtDesc("release-123"))
                .thenReturn(discussions);

        List<Discussion> result = discussionService.getDiscussionsByRelease("release-123");

        assertEquals(1, result.size());
    }

    @Test
    void deleteDiscussion_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        discussionService.deleteDiscussion("discussion-123", "user-123");

        verify(discussionRepository).delete(testDiscussion);
    }

    @Test
    void deleteDiscussion_NotAuthor_ThrowsException() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        assertThrows(RuntimeException.class, () -> 
            discussionService.deleteDiscussion("discussion-123", "different-user"));
    }
}
