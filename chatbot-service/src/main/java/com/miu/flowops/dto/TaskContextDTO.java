package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskContextDTO {
    
    private String id;
    
    private String title;
    
    private String description;
    
    private String status;
    
    private String releaseId;
    
    private String developerId;
    
    private Integer orderIndex;
    
    // For backward compatibility with service layer
    public String getAssignedTo() {
        return developerId;
    }
}
