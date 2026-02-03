package com.miu.flowops.repository;

import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {
    
    List<Discussion> findByReleaseId(String releaseId);
    
    List<Discussion> findByTaskId(String taskId);
    
    List<Discussion> findByAuthorId(String authorId);
    
    List<Discussion> findByReleaseIdAndStatus(String releaseId, DiscussionStatus status);
    
    List<Discussion> findByTaskIdAndStatus(String taskId, DiscussionStatus status);
    
    List<Discussion> findByStatus(DiscussionStatus status);
    
    List<Discussion> findByReleaseIdOrderByCreatedAtDesc(String releaseId);
    
    List<Discussion> findByTaskIdOrderByCreatedAtDesc(String taskId);
    
    long countByReleaseIdAndStatus(String releaseId, DiscussionStatus status);
    
    long countByTaskIdAndStatus(String taskId, DiscussionStatus status);
}
