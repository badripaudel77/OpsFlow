package com.miu.flowops.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "discussions")
public class Discussion {
    @Id
    private String id;

    private String title;

    private String content;

    private String authorId;

    private String authorName;

    // Reference to the associated release
    private String releaseId;

    // Reference to the associated task (optional)
    private String taskId;

    private DiscussionStatus status;

    private DiscussionType type;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private String resolvedBy;
}
