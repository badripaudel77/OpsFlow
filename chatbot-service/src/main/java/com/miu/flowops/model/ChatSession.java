package com.miu.flowops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_sessions")
public class ChatSession {
    
    @Id
    private String id;
    
    private String userId;
    
    private String title;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastMessageAt;
    
    private boolean active;
    
    public ChatSession(String userId, String title) {
        this.userId = userId;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
        this.active = true;
    }
}
