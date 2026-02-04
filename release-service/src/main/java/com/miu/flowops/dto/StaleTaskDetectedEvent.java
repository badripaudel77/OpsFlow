package com.miu.flowops.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaleTaskDetectedEvent {
    private String developerId;
    private String email;
    private String taskId;
    private String taskTitle;
    private String message;
}
