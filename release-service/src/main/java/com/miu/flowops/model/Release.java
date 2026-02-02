package com.miu.flowops.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "releases")
public class Release {
    @Id
    private String id;

    private String title;

    private Boolean isCompleted;

    private List<Task> tasks;
}

