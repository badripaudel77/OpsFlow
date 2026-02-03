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
@RequestMapping("/api/discussions/{discussionId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable String discussionId,
            @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(discussionId, request));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable String discussionId) {
        return ResponseEntity.ok(commentService.getComments(discussionId));
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

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String discussionId,
            @PathVariable String commentId,
            @RequestHeader("X-User-Id") String userId) {
        commentService.deleteComment(discussionId, commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
