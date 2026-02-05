package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotfixTaskAddedEvent implements Serializable {
    private String developerId;
    private String taskId;
    private String releaseId;
    private String taskTitle;
    private String email;
    private String message;
}
