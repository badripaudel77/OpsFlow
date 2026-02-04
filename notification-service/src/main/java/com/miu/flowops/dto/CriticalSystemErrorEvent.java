package com.miu.flowops.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalSystemErrorEvent {
    private String serviceName;
    private String errorMessage;
    private String severity;
    private LocalDateTime occurredAt;
    private List<String> recipientEmails;
}