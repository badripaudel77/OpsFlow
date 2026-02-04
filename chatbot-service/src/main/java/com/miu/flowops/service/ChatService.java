package com.miu.flowops.service;

import com.miu.flowops.dto.*;
import com.miu.flowops.model.ChatMessage;
import com.miu.flowops.model.ChatSession;
import com.miu.flowops.repository.ChatMessageRepository;
import com.miu.flowops.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final OllamaService ollamaService;
    private final ReleaseContextService releaseContextService;
    
    @Value("${chat.context.max-messages:5}")
    private int maxContextMessages;
    
    @Transactional
    public SessionResponse createSession(String userId, String title) {
        log.info("Creating chat session for user: {}", userId);
        
        String sessionTitle = (title != null && !title.isBlank()) 
                ? title 
                : "Chat Session - " + LocalDateTime.now();
        
        ChatSession session = new ChatSession(userId, sessionTitle);
        session = sessionRepository.save(session);
        
        log.info("Created session: {} for user: {}", session.getId(), userId);
        
        return new SessionResponse(
                session.getId(),
                session.getUserId(),
                session.getTitle(),
                session.getCreatedAt()
        );
    }
    
    @Transactional
    public MessageResponse sendMessage(String sessionId, String userId, String messageContent) {
        log.info("Processing message for session: {} from user: {}", sessionId, userId);
        
        // Verify session exists and belongs to user
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found or access denied"));
        
        if (!session.isActive()) {
            throw new RuntimeException("Session is not active");
        }
        
        // Save user message
        ChatMessage userMessage = new ChatMessage(sessionId, "user", messageContent);
        userMessage = messageRepository.save(userMessage);
        
        // Build context and generate AI response
        String contextualPrompt = buildContextualPrompt(sessionId, userId, messageContent);
        String aiResponse = ollamaService.generateResponse(contextualPrompt);
        
        // Save assistant message
        ChatMessage assistantMessage = new ChatMessage(sessionId, "assistant", aiResponse);
        assistantMessage = messageRepository.save(assistantMessage);
        
        // Update session last message time
        session.setLastMessageAt(LocalDateTime.now());
        sessionRepository.save(session);
        
        log.info("Generated response for session: {}", sessionId);
        
        return new MessageResponse(
                sessionId,
                new MessageDTO(userMessage.getId(), userMessage.getContent(), userMessage.getTimestamp()),
                new MessageDTO(assistantMessage.getId(), assistantMessage.getContent(), assistantMessage.getTimestamp())
        );
    }
    
    public ChatHistoryResponse getChatHistory(String sessionId, String userId, int limit, int offset) {
        log.info("Fetching chat history for session: {}", sessionId);
        
        // Verify session belongs to user
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found or access denied"));
        
        PageRequest pageRequest = PageRequest.of(
                offset / limit,
                limit,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );
        
        Page<ChatMessage> messagePage = messageRepository.findBySessionIdOrderByTimestampDesc(sessionId, pageRequest);
        
        // Reverse to show oldest first
        List<ChatMessageDTO> messages = messagePage.getContent().stream()
                .map(msg -> new ChatMessageDTO(msg.getId(), msg.getRole(), msg.getContent(), msg.getTimestamp()))
                .collect(Collectors.toList());
        Collections.reverse(messages);
        
        long total = messageRepository.countBySessionId(sessionId);
        boolean hasMore = (offset + limit) < total;
        
        return new ChatHistoryResponse(sessionId, messages, (int) total, hasMore);
    }
    
    private String buildContextualPrompt(String sessionId, String userId, String currentMessage) {
        StringBuilder prompt = new StringBuilder();
        
        // System instructions
        prompt.append("System: You are an AI assistant for OpsFlow, a release management platform. ");
        prompt.append("Help developers with questions about tasks, releases, and workflows. ");
        prompt.append("Be concise and helpful.\n\n");
        
        // Add user context from database
        String userContext = releaseContextService.buildContextForUser(userId);
        if (!userContext.isBlank()) {
            prompt.append("User Context:\n");
            prompt.append(userContext);
            prompt.append("\n");
        }
        
        // Add conversation history (last N messages)
        List<ChatMessage> recentMessages = messageRepository.findTop5BySessionIdOrderByTimestampDesc(sessionId);
        Collections.reverse(recentMessages);  // Show oldest first
        
        if (!recentMessages.isEmpty()) {
            prompt.append("Previous conversation:\n");
            for (ChatMessage msg : recentMessages) {
                prompt.append(String.format("%s: %s\n", 
                        msg.getRole().equals("user") ? "User" : "Assistant", 
                        msg.getContent()));
            }
            prompt.append("\n");
        }
        
        // Current message
        prompt.append("User: ");
        prompt.append(currentMessage);
        prompt.append("\n\nAssistant:");
        
        return prompt.toString();
    }
    
    public List<SessionResponse> getUserSessions(String userId) {
        log.info("Fetching sessions for user: {}", userId);
        
        return sessionRepository.findByUserIdOrderByLastMessageAtDesc(userId).stream()
                .map(session -> new SessionResponse(
                        session.getId(),
                        session.getUserId(),
                        session.getTitle(),
                        session.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
