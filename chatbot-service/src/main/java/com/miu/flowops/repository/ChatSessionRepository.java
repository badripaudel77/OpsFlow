package com.miu.flowops.repository;

import com.miu.flowops.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    
    List<ChatSession> findByUserIdOrderByLastMessageAtDesc(String userId);
    
    Optional<ChatSession> findByIdAndUserId(String id, String userId);
    
    List<ChatSession> findByUserIdAndActiveTrue(String userId);
}
