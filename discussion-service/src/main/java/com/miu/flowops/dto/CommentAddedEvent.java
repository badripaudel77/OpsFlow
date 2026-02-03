package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAddedEvent {
    private String discussionId;
    private String discussionTitle;
    private String commentId;
    private String authorId;
    private String authorName;
    private String releaseId;
    private String taskId;
    private String message;
}
