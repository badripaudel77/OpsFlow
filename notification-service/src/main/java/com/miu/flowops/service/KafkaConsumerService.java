package com.miu.flowops.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_TASK_ASSIGNED = "task-assigned-topic";
    private static final String TOPIC_TASK_COMPLETED = "task-completed-topic";
    private static final String TOPIC_HOTFIX_ADDED = "hotfix-task-added-topic";

   // listen ..... and send email
}
