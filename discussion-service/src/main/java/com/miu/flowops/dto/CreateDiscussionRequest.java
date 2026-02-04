package com.miu.flowops.dto;

import com.miu.flowops.model.DiscussionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiscussionRequest {
    private String title;
    private String content;
    private String authorId;
    private String releaseId;
    private String taskId;
    private DiscussionType type;
}
