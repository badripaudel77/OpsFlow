package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequest {
    private String content;
    private String authorId;
    private String authorName;
    private String parentId;  // Optional: ID of parent comment for replies (null for top-level)
}
