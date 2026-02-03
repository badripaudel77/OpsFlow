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
public class UpdateDiscussionRequest {
    private String title;
    private String content;
    private DiscussionType type;
}
