package com.miu.flowops.controller;

import com.miu.flowops.dto.AddCommentRequest;
import com.miu.flowops.dto.UpdateCommentRequest;
import com.miu.flowops.model.Comment;
import com.miu.flowops.service.impl.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discussions/{discussionId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Add a top-level comment or reply to an existing comment.
     * To reply to a comment, include "parentId" in the request body.
     */
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable String discussionId,
            @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(discussionId, request));
    }

    /**
     * Reply to a specific comment (alternative endpoint).
     * The parentId is taken from the path.
     */
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Comment> replyToComment(
            @PathVariable String discussionId,
            @PathVariable String commentId,
            @RequestBody AddCommentRequest request) {
        // Set the parentId from path if not already set
        if (request.getParentId() == null || request.getParentId().isEmpty()) {
            request.setParentId(commentId);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(discussionId, request));
    }

    /**
     * Get all top-level comments with nested replies (threaded view).
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable String discussionId) {
        return ResponseEntity.ok(commentService.getComments(discussionId));
    }

    /**
     * Get all comments in a flat list (no nesting).
     */
    @GetMapping("/flat")
    public ResponseEntity<List<Comment>> getCommentsFlat(@PathVariable String discussionId) {
        return ResponseEntity.ok(commentService.getCommentsFlat(discussionId));
    }

    /**
     * Get replies for a specific comment.
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getReplies(
            @PathVariable String discussionId,
            @PathVariable String commentId) {
        return ResponseEntity.ok(commentService.getReplies(discussionId, commentId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(
            @PathVariable String discussionId,
            @PathVariable String commentId) {
        return ResponseEntity.ok(commentService.getComment(discussionId, commentId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable String discussionId,
            @PathVariable String commentId,
            @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(discussionId, commentId, request));
    }

    /**
     * Delete a comment.
     * If the comment has replies, it will be soft-deleted (content replaced with [deleted]).
     * If no replies, it will be hard-deleted.
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String discussionId,
            @PathVariable String commentId,
            @RequestHeader("X-User-Id") String userId) {
        commentService.deleteComment(discussionId, commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
