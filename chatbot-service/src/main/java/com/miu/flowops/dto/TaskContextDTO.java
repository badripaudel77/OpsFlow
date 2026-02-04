package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class TaskContextDTO {
    
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    private String status;
    
    private String releaseId;
    
    private String assignedTo;
    
    private Integer orderIndex;
}
