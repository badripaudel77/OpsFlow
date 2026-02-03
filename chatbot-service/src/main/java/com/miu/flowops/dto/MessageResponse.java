package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String sessionId;
    private MessageDTO userMessage;
    private MessageDTO assistantMessage;
}
