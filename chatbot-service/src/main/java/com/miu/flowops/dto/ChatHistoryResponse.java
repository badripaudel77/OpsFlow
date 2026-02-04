package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponse {
    
    private String sessionId;
    private List<ChatMessageDTO> messages;
    private int total;
    private boolean hasMore;
}
