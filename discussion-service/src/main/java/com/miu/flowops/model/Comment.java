package com.miu.flowops.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;

    private String content;

    private String authorId;

    private String authorName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isEdited;
}
