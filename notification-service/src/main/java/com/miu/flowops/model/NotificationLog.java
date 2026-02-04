package com.miu.flowops.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification_logs")
public class NotificationLog {
    @Id
    private String id;

    private String notificationType;
    private String recipientEmail;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    private String status;
    private String eventPayload;
    private String errorMessage;    
}