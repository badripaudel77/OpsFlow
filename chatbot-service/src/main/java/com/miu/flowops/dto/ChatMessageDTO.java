package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private String id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
}
