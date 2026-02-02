package com.miu.flowops.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private String developerId;
    private Integer orderIndex;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

