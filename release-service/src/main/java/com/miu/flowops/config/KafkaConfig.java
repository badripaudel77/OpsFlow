package com.miu.flowops.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KafkaConfig {

    // Since, these topics are being used in other services as well, it can also be centralized.
    private static final String TOPIC_TASK_ASSIGNED = "task-assigned-topic";
    private static final String TOPIC_TASK_COMPLETED = "task-completed-topic";
    private static final String TOPIC_HOTFIX_ADDED = "hotfix-task-added-topic";

    @Bean
    public NewTopic taskAssignedTopic() {
        log.debug("Creating TOPIC : {} ", TOPIC_TASK_ASSIGNED);
        return new NewTopic(TOPIC_TASK_ASSIGNED, 1, (short) 1);
    }

    @Bean
    public NewTopic taskCompletedTopic() {
        log.debug("Creating TOPIC : {} ", TOPIC_TASK_COMPLETED);
        return new NewTopic(TOPIC_TASK_COMPLETED, 1, (short) 1);
    }

    @Bean
    public NewTopic taskHotFixAddedTopic() {
        log.debug("Creating TOPIC : {} ", TOPIC_HOTFIX_ADDED);
        return new NewTopic(TOPIC_HOTFIX_ADDED, 1, (short) 1);
    }

}