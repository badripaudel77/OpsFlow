package com.miu.flowops.repository;

import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReleaseRepository extends MongoRepository<Release, String> {
    
    @Query(value = "{ 'tasks': { $elemMatch: { 'developerId': ?0, 'status': ?1 } } }", exists = true)
    boolean existsByDeveloperIdAndTaskStatus(String developerId, TaskStatus status);

    // Use Spring Data's derived query method
    @Aggregation(pipeline = {
            "{ $unwind: '$tasks' }",
            "{ $match: { 'tasks.status': ?0, 'tasks.startedAt': { $lte: ?1 } } }",
            "{ $replaceRoot: { newRoot: '$tasks' } }"
    })
    List<Task> findTasksByStatusAndStartedBefore(TaskStatus status, LocalDateTime time);

}
