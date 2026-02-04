package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotfixTaskAddedEvent {
    private String developerId;
    private String releaseId;
    private String taskTitle;
    private String email;
    private String message;
}
