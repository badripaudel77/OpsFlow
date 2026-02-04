package com.miu.flowops.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String parentId;  // null for top-level comments, commentId for replies

    @Builder.Default
    private List<Comment> replies = new ArrayList<>();  // Nested replies

    private Integer depth;  // Nesting level (0 for top-level)

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isEdited;

    private Boolean isDeleted;  // Soft delete to preserve thread structure
}
