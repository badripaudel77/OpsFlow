package com.miu.flowops.service.impl;

import com.miu.flowops.dto.AddCommentRequest;
import com.miu.flowops.dto.CommentAddedEvent;
import com.miu.flowops.dto.UpdateCommentRequest;
import com.miu.flowops.model.Comment;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.ICommentService;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService implements ICommentService {

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Comment addComment(String discussionId, AddCommentRequest request) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        // Check if discussion is closed
        if (discussion.getStatus() == DiscussionStatus.CLOSED) {
            throw new RuntimeException("Cannot add comments to a closed discussion");
        }

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .content(request.getContent())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isEdited(false)
                .build();

        discussion.getComments().add(comment);
        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);

        // Publish event
        kafkaProducerService.sendCommentAddedEvent(CommentAddedEvent.builder()
                .discussionId(discussionId)
                .discussionTitle(discussion.getTitle())
                .commentId(comment.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .releaseId(discussion.getReleaseId())
                .taskId(discussion.getTaskId())
                .message("New comment added to discussion: " + discussion.getTitle())
                .build());

        log.info("Comment added to discussion: {}", discussionId);
        return comment;
    }

    @Override
    public Comment updateComment(String discussionId, String commentId, UpdateCommentRequest request) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        Comment comment = findComment(discussion, commentId);

        // Verify ownership
        if (!comment.getAuthorId().equals(request.getAuthorId())) {
            throw new RuntimeException("Only the comment author can update this comment");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setIsEdited(true);

        discussionRepository.save(discussion);
        log.info("Comment updated: {} in discussion: {}", commentId, discussionId);
        return comment;
    }

    @Override
    public void deleteComment(String discussionId, String commentId, String userId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        Comment comment = findComment(discussion, commentId);

        // Verify ownership (author of comment or discussion author can delete)
        if (!comment.getAuthorId().equals(userId) && !discussion.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        discussion.getComments().removeIf(c -> c.getId().equals(commentId));
        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);
        
        log.info("Comment deleted: {} from discussion: {}", commentId, discussionId);
    }

    @Override
    public List<Comment> getComments(String discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        return discussion.getComments();
    }

    @Override
    public Comment getComment(String discussionId, String commentId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        return findComment(discussion, commentId);
    }

    private Comment findComment(Discussion discussion, String commentId) {
        return discussion.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
