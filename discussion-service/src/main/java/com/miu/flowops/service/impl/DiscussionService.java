package com.miu.flowops.service.impl;

import com.miu.flowops.dto.*;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.service.IDiscussionService;
import com.miu.flowops.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussionService implements IDiscussionService {

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Discussion createDiscussion(CreateDiscussionRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Discussion discussion = Discussion.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .releaseId(request.getReleaseId())
                .taskId(request.getTaskId())
                .type(request.getType())
                .status(DiscussionStatus.OPEN)
                .comments(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Discussion savedDiscussion = discussionRepository.save(discussion);

        // Publish event
        kafkaProducerService.sendDiscussionCreatedEvent(DiscussionCreatedEvent.builder()
                .discussionId(savedDiscussion.getId())
                .title(savedDiscussion.getTitle())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .releaseId(savedDiscussion.getReleaseId())
                .taskId(savedDiscussion.getTaskId())
                .type(savedDiscussion.getType().name())
                .message("New discussion created: " + savedDiscussion.getTitle())
                .build());

        log.info("Discussion created with ID: {}", savedDiscussion.getId());
        return savedDiscussion;
    }

    @Override
    public Discussion getDiscussion(String discussionId) {
        return discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
    }

    @Override
    public DiscussionResponse getDiscussionWithDetails(String discussionId) {
        Discussion discussion = getDiscussion(discussionId);
        return mapToResponse(discussion);
    }

    @Override
    public Discussion updateDiscussion(String discussionId, UpdateDiscussionRequest request, String userId) {
        Discussion discussion = getDiscussion(discussionId);

        // Verify ownership
        if (!discussion.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only the author can update this discussion");
        }

        if (request.getTitle() != null) {
            discussion.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            discussion.setContent(request.getContent());
        }
        if (request.getType() != null) {
            discussion.setType(request.getType());
        }
        discussion.setUpdatedAt(LocalDateTime.now());

        return discussionRepository.save(discussion);
    }

    @Override
    public void deleteDiscussion(String discussionId, String userId) {
        Discussion discussion = getDiscussion(discussionId);

        // Verify ownership
        if (!discussion.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only the author can delete this discussion");
        }

        discussionRepository.delete(discussion);
        log.info("Discussion deleted with ID: {}", discussionId);
    }

    @Override
    public List<Discussion> getDiscussionsByRelease(String releaseId) {
        return discussionRepository.findByReleaseIdOrderByCreatedAtDesc(releaseId);
    }

    @Override
    public List<Discussion> getDiscussionsByTask(String taskId) {
        return discussionRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    @Override
    public List<Discussion> getDiscussionsByAuthor(String authorId) {
        return discussionRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Discussion> getDiscussionsByStatus(DiscussionStatus status) {
        return discussionRepository.findByStatus(status);
    }

    @Override
    public Discussion resolveDiscussion(String discussionId, String userId) {
        Discussion discussion = getDiscussion(discussionId);
        User resolver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (discussion.getStatus() == DiscussionStatus.RESOLVED) {
            throw new RuntimeException("Discussion is already resolved");
        }

        discussion.setStatus(DiscussionStatus.RESOLVED);
        discussion.setResolvedAt(LocalDateTime.now());
        discussion.setResolvedBy(resolver.getFullName());
        discussion.setUpdatedAt(LocalDateTime.now());

        Discussion savedDiscussion = discussionRepository.save(discussion);

        // Publish event
        kafkaProducerService.sendDiscussionResolvedEvent(DiscussionResolvedEvent.builder()
                .discussionId(savedDiscussion.getId())
                .title(savedDiscussion.getTitle())
                .resolvedById(userId)
                .resolvedByName(resolver.getFullName())
                .authorId(savedDiscussion.getAuthorId())
                .releaseId(savedDiscussion.getReleaseId())
                .taskId(savedDiscussion.getTaskId())
                .message("Discussion resolved: " + savedDiscussion.getTitle())
                .build());

        log.info("Discussion resolved with ID: {}", discussionId);
        return savedDiscussion;
    }

    @Override
    public Discussion reopenDiscussion(String discussionId, String userId) {
        Discussion discussion = getDiscussion(discussionId);

        if (discussion.getStatus() == DiscussionStatus.OPEN) {
            throw new RuntimeException("Discussion is already open");
        }

        discussion.setStatus(DiscussionStatus.OPEN);
        discussion.setResolvedAt(null);
        discussion.setResolvedBy(null);
        discussion.setUpdatedAt(LocalDateTime.now());

        log.info("Discussion reopened with ID: {}", discussionId);
        return discussionRepository.save(discussion);
    }

    @Override
    public Discussion closeDiscussion(String discussionId, String userId) {
        Discussion discussion = getDiscussion(discussionId);

        // Only author or admin can close
        if (!discussion.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only the author can close this discussion");
        }

        discussion.setStatus(DiscussionStatus.CLOSED);
        discussion.setUpdatedAt(LocalDateTime.now());

        log.info("Discussion closed with ID: {}", discussionId);
        return discussionRepository.save(discussion);
    }

    private DiscussionResponse mapToResponse(Discussion discussion) {
        return DiscussionResponse.builder()
                .id(discussion.getId())
                .title(discussion.getTitle())
                .content(discussion.getContent())
                .authorId(discussion.getAuthorId())
                .authorName(discussion.getAuthorName())
                .releaseId(discussion.getReleaseId())
                .taskId(discussion.getTaskId())
                .status(discussion.getStatus())
                .type(discussion.getType())
                .comments(discussion.getComments())
                .commentCount(discussion.getComments() != null ? discussion.getComments().size() : 0)
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt())
                .resolvedAt(discussion.getResolvedAt())
                .resolvedBy(discussion.getResolvedBy())
                .build();
    }
}
