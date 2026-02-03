package com.miu.flowops.service;

import com.miu.flowops.dto.CommentAddedEvent;
import com.miu.flowops.dto.DiscussionCreatedEvent;
import com.miu.flowops.dto.DiscussionResolvedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String TOPIC_DISCUSSION_CREATED = "discussion-created-topic";
    private static final String TOPIC_COMMENT_ADDED = "comment-added-topic";
    private static final String TOPIC_DISCUSSION_RESOLVED = "discussion-resolved-topic";

    public void sendDiscussionCreatedEvent(DiscussionCreatedEvent event) {
        log.info("Publishing DiscussionCreatedEvent: {}", event);
        kafkaTemplate.send(TOPIC_DISCUSSION_CREATED, event);
    }

    public void sendCommentAddedEvent(CommentAddedEvent event) {
        log.info("Publishing CommentAddedEvent: {}", event);
        kafkaTemplate.send(TOPIC_COMMENT_ADDED, event);
    }

    public void sendDiscussionResolvedEvent(DiscussionResolvedEvent event) {
        log.info("Publishing DiscussionResolvedEvent: {}", event);
        kafkaTemplate.send(TOPIC_DISCUSSION_RESOLVED, event);
    }
}
