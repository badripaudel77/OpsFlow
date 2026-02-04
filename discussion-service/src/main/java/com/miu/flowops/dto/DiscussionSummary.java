package com.miu.flowops.dto;

import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.model.DiscussionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for discussion list view without comments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionSummary {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private String releaseId;
    private String taskId;
    private DiscussionStatus status;
    private DiscussionType type;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
}
