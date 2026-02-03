package com.miu.flowops.controller;

import com.miu.flowops.dto.*;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.service.impl.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping
    public ResponseEntity<Discussion> createDiscussion(@RequestBody CreateDiscussionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discussionService.createDiscussion(request));
    }

    @GetMapping("/{discussionId}")
    public ResponseEntity<DiscussionResponse> getDiscussion(@PathVariable String discussionId) {
        return ResponseEntity.ok(discussionService.getDiscussionWithDetails(discussionId));
    }

    @PutMapping("/{discussionId}")
    public ResponseEntity<Discussion> updateDiscussion(
            @PathVariable String discussionId,
            @RequestBody UpdateDiscussionRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(discussionService.updateDiscussion(discussionId, request, userId));
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable String discussionId,
            @RequestHeader("X-User-Id") String userId) {
        discussionService.deleteDiscussion(discussionId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/release/{releaseId}")
    public ResponseEntity<List<Discussion>> getDiscussionsByRelease(@PathVariable String releaseId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByRelease(releaseId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Discussion>> getDiscussionsByTask(@PathVariable String taskId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByTask(taskId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Discussion>> getDiscussionsByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByAuthor(authorId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Discussion>> getDiscussionsByStatus(@PathVariable DiscussionStatus status) {
        return ResponseEntity.ok(discussionService.getDiscussionsByStatus(status));
    }

    @PostMapping("/{discussionId}/resolve")
    public ResponseEntity<Discussion> resolveDiscussion(
            @PathVariable String discussionId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(discussionService.resolveDiscussion(discussionId, userId));
    }

    @PostMapping("/{discussionId}/reopen")
    public ResponseEntity<Discussion> reopenDiscussion(
            @PathVariable String discussionId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(discussionService.reopenDiscussion(discussionId, userId));
    }

    @PostMapping("/{discussionId}/close")
    public ResponseEntity<Discussion> closeDiscussion(
            @PathVariable String discussionId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(discussionService.closeDiscussion(discussionId, userId));
    }
}
