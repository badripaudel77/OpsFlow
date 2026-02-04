package com.miu.flowops.service;

import com.miu.flowops.dto.AddCommentRequest;
import com.miu.flowops.dto.UpdateCommentRequest;
import com.miu.flowops.model.*;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.impl.CommentService;
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
class CommentServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Discussion testDiscussion;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .username("testuser")
                .fullName("Test User")
                .roles(Set.of(Role.DEVELOPER))
                .build();

        testComment = Comment.builder()
                .id("comment-123")
                .content("Test comment content")
                .authorId("user-123")
                .authorName("Test User")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isEdited(false)
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
                .comments(new ArrayList<>(List.of(testComment)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addComment_Success() {
        AddCommentRequest request = AddCommentRequest.builder()
                .content("New comment")
                .authorId("user-123")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        Comment result = commentService.addComment("discussion-123", request);

        assertNotNull(result);
        assertEquals("New comment", result.getContent());
        verify(kafkaProducerService).sendCommentAddedEvent(any());
    }

    @Test
    void addComment_ClosedDiscussion_ThrowsException() {
        testDiscussion.setStatus(DiscussionStatus.CLOSED);

        AddCommentRequest request = AddCommentRequest.builder()
                .content("New comment")
                .authorId("user-123")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        assertThrows(RuntimeException.class, () -> 
            commentService.addComment("discussion-123", request));
    }

    @Test
    void updateComment_Success() {
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Updated content")
                .authorId("user-123")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        Comment result = commentService.updateComment("discussion-123", "comment-123", request);

        assertNotNull(result);
        assertTrue(result.getIsEdited());
    }

    @Test
    void updateComment_NotAuthor_ThrowsException() {
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Updated content")
                .authorId("different-user")
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        assertThrows(RuntimeException.class, () -> 
            commentService.updateComment("discussion-123", "comment-123", request));
    }

    @Test
    void deleteComment_ByCommentAuthor_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(Discussion.class))).thenReturn(testDiscussion);

        commentService.deleteComment("discussion-123", "comment-123", "user-123");

        verify(discussionRepository).save(any(Discussion.class));
    }

    @Test
    void deleteComment_NotAuthorized_ThrowsException() {
        Discussion discussionWithDifferentAuthor = Discussion.builder()
                .id("discussion-123")
                .authorId("other-user")
                .comments(new ArrayList<>(List.of(testComment)))
                .status(DiscussionStatus.OPEN)
                .build();

        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(discussionWithDifferentAuthor));

        assertThrows(RuntimeException.class, () -> 
            commentService.deleteComment("discussion-123", "comment-123", "unauthorized-user"));
    }

    @Test
    void getComments_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        List<Comment> result = commentService.getComments("discussion-123");

        assertEquals(1, result.size());
        assertEquals("comment-123", result.get(0).getId());
    }

    @Test
    void getComment_Success() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        Comment result = commentService.getComment("discussion-123", "comment-123");

        assertNotNull(result);
        assertEquals("comment-123", result.getId());
    }

    @Test
    void getComment_NotFound_ThrowsException() {
        when(discussionRepository.findById("discussion-123")).thenReturn(Optional.of(testDiscussion));

        assertThrows(RuntimeException.class, () -> 
            commentService.getComment("discussion-123", "unknown-comment"));
    }
}
