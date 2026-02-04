package com.miu.flowops.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.miu.flowops.model.NotificationLog;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String>{

}