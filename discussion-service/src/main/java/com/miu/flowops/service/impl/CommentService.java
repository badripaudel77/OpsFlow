package com.miu.flowops.service.impl;

import com.miu.flowops.dto.AddCommentRequest;
import com.miu.flowops.dto.CommentAddedEvent;
import com.miu.flowops.dto.UpdateCommentRequest;
import com.miu.flowops.model.Comment;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.service.ICommentService;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService implements ICommentService {

    private static final int MAX_REPLY_DEPTH = 10;  // Limit nesting depth like Reddit

    private final DiscussionRepository discussionRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Comment addComment(String discussionId, AddCommentRequest request) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        // Check if discussion is closed
        if (discussion.getStatus() == DiscussionStatus.CLOSED) {
            throw new RuntimeException("Cannot add comments to a closed discussion");
        }

        // Use authorId and authorName directly from request
        String authorId = request.getAuthorId();
        String authorName = request.getAuthorName() != null ? request.getAuthorName() : "Unknown User";

        int depth = 0;
        String parentId = request.getParentId();

        // If this is a reply, validate parent exists and calculate depth
        if (parentId != null && !parentId.isEmpty()) {
            Comment parentComment = findCommentRecursively(discussion.getComments(), parentId);
            if (parentComment == null) {
                throw new RuntimeException("Parent comment not found");
            }
            depth = (parentComment.getDepth() != null ? parentComment.getDepth() : 0) + 1;
            if (depth > MAX_REPLY_DEPTH) {
                throw new RuntimeException("Maximum reply depth exceeded. Consider starting a new thread.");
            }
        }

        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .content(request.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .parentId(parentId)
                .replies(new ArrayList<>())
                .depth(depth)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isEdited(false)
                .isDeleted(false)
                .build();

        // Add to parent's replies or to top-level comments
        if (parentId != null && !parentId.isEmpty()) {
            addReplyToParent(discussion.getComments(), parentId, comment);
        } else {
            discussion.getComments().add(comment);
        }

        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);

        // Publish event
        String eventMessage = parentId != null ? 
                "New reply added to comment in discussion: " + discussion.getTitle() :
                "New comment added to discussion: " + discussion.getTitle();
        
        kafkaProducerService.sendCommentAddedEvent(CommentAddedEvent.builder()
                .discussionId(discussionId)
                .discussionTitle(discussion.getTitle())
                .commentId(comment.getId())
                .authorId(authorId)
                .authorName(authorName)
                .releaseId(discussion.getReleaseId())
                .taskId(discussion.getTaskId())
                .message(eventMessage)
                .build());

        log.info("Comment added to discussion: {} (parentId: {})", discussionId, parentId);
        return comment;
    }

    /**
     * Recursively find a comment by ID in the nested structure
     */
    private Comment findCommentRecursively(List<Comment> comments, String commentId) {
        for (Comment comment : comments) {
            if (comment.getId().equals(commentId)) {
                return comment;
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                Comment found = findCommentRecursively(comment.getReplies(), commentId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Add a reply to a parent comment
     */
    private boolean addReplyToParent(List<Comment> comments, String parentId, Comment reply) {
        for (Comment comment : comments) {
            if (comment.getId().equals(parentId)) {
                if (comment.getReplies() == null) {
                    comment.setReplies(new ArrayList<>());
                }
                comment.getReplies().add(reply);
                return true;
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                if (addReplyToParent(comment.getReplies(), parentId, reply)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Comment updateComment(String discussionId, String commentId, UpdateCommentRequest request) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        Comment comment = findCommentRecursively(discussion.getComments(), commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        // Check if comment is deleted
        if (Boolean.TRUE.equals(comment.getIsDeleted())) {
            throw new RuntimeException("Cannot update a deleted comment");
        }

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

        Comment comment = findCommentRecursively(discussion.getComments(), commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        // Verify ownership (author of comment or discussion author can delete)
        if (!comment.getAuthorId().equals(userId) && !discussion.getAuthorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        // Soft delete to preserve thread structure (like Reddit's [deleted])
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            comment.setContent("[deleted]");
            comment.setAuthorName("[deleted]");
            comment.setIsDeleted(true);
            comment.setUpdatedAt(LocalDateTime.now());
        } else {
            // No replies, can hard delete - remove from parent or top level
            removeCommentRecursively(discussion.getComments(), commentId);
        }

        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);
        
        log.info("Comment deleted: {} from discussion: {}", commentId, discussionId);
    }

    /**
     * Recursively remove a comment from the nested structure
     */
    private boolean removeCommentRecursively(List<Comment> comments, String commentId) {
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            if (comment.getId().equals(commentId)) {
                comments.remove(i);
                return true;
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                if (removeCommentRecursively(comment.getReplies(), commentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Comment> getComments(String discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        // Return only top-level comments (with nested replies included)
        return discussion.getComments().stream()
                .filter(c -> c.getParentId() == null || c.getParentId().isEmpty())
                .toList();
    }

    /**
     * Get all comments in a flat list (for backward compatibility)
     */
    public List<Comment> getCommentsFlat(String discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        List<Comment> flatList = new ArrayList<>();
        flattenComments(discussion.getComments(), flatList);
        return flatList;
    }

    private void flattenComments(List<Comment> comments, List<Comment> flatList) {
        for (Comment comment : comments) {
            flatList.add(comment);
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                flattenComments(comment.getReplies(), flatList);
            }
        }
    }

    /**
     * Get replies for a specific comment
     */
    public List<Comment> getReplies(String discussionId, String commentId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        Comment comment = findCommentRecursively(discussion.getComments(), commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        return comment.getReplies() != null ? comment.getReplies() : new ArrayList<>();
    }

    @Override
    public Comment getComment(String discussionId, String commentId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
        Comment comment = findCommentRecursively(discussion.getComments(), commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        return comment;
    }
}
