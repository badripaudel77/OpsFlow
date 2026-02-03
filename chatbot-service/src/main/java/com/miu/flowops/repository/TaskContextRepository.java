package com.miu.flowops.repository;

import com.miu.flowops.dto.TaskContextDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskContextRepository extends MongoRepository<TaskContextDTO, String> {
    
    List<TaskContextDTO> findByReleaseId(String releaseId);
    
    List<TaskContextDTO> findByAssignedTo(String userId);
    
    List<TaskContextDTO> findByStatus(String status);
    
    List<TaskContextDTO> findByReleaseIdOrderByOrderIndexAsc(String releaseId);
}
