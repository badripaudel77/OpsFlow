package com.miu.flowops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "releases")
public class ReleaseContextDTO {
    
    @Id
    private String id;
    
    private String title;
    
    private Boolean isCompleted;
    
    private List<EmbeddedTask> tasks;
    
    // For backward compatibility
    public String getName() {
        return title;
    }
    
    public boolean isCompleted() {
        return Boolean.TRUE.equals(isCompleted);
    }
    
    public String getStatus() {
        return Boolean.TRUE.equals(isCompleted) ? "COMPLETED" : "IN_PROGRESS";
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbeddedTask {
        private String id;
        private String title;
        private String description;
        private String status;
        private String developerId;
        private Integer orderIndex;
    }
}
