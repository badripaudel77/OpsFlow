package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaleTaskReminderEvent {
    private String developerId;
    private String email;
    private String taskId;
    private String taskTitle;
    private String message;
}
