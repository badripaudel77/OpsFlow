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
    
    private String name;
    
    private String description;
    
    private String status;
    
    private boolean completed;
    
    private List<String> taskIds;
}
