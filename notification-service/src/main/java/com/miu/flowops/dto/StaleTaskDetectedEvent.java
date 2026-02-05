package com.miu.flowops.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StaleTaskDetectedEvent implements Serializable {
    private String developerId;
    private String email;
    private String taskId;
    private String taskTitle;
    private String message;
}
