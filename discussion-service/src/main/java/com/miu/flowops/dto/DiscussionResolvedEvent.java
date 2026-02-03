package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionResolvedEvent {
    private String discussionId;
    private String title;
    private String resolvedById;
    private String resolvedByName;
    private String authorId;
    private String releaseId;
    private String taskId;
    private String message;
}
