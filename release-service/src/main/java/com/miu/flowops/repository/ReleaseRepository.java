package com.miu.flowops.repository;

import com.miu.flowops.model.Release;
import com.miu.flowops.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReleaseRepository extends MongoRepository<Release, String> {
    
    @Query(value = "{ 'tasks': { $elemMatch: { 'developerId': ?0, 'status': ?1 } } }", exists = true)
    boolean existsByDeveloperIdAndTaskStatus(String developerId, TaskStatus status);
}
