package com.miu.flowops.dto;

import com.miu.flowops.model.Comment;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.model.DiscussionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private String releaseId;
    private String taskId;
    private DiscussionStatus status;
    private DiscussionType type;
    private List<Comment> comments;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
}
