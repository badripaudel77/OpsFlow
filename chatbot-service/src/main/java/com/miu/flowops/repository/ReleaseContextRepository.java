package com.miu.flowops.repository;

import com.miu.flowops.dto.ReleaseContextDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReleaseContextRepository extends MongoRepository<ReleaseContextDTO, String> {
    
    List<ReleaseContextDTO> findByCompletedFalse();
    
    List<ReleaseContextDTO> findByStatus(String status);
}
