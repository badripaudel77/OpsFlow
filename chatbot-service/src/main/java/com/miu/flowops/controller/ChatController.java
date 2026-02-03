package com.miu.flowops.controller;

import com.miu.flowops.dto.*;
import com.miu.flowops.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping(value = "/session", consumes = {"application/json", "application/*+json", "*/*"})
    public ResponseEntity<SessionResponse> createSession(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) CreateSessionRequest request) {
        
        log.info("Creating session for user: {}", userId);
        
        String title = request != null ? request.getTitle() : null;
        SessionResponse response = chatService.createSession(userId, title);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponse>> getUserSessions(
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Fetching sessions for user: {}", userId);
        
        List<SessionResponse> sessions = chatService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }
    
    @PostMapping("/{sessionId}/message")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SendMessageRequest request) {
        
        log.info("User {} sending message to session: {}", userId, sessionId);
        
        MessageResponse response = chatService.sendMessage(sessionId, userId, request.getMessage());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{sessionId}/history")
    public ResponseEntity<ChatHistoryResponse> getChatHistory(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        log.info("User {} fetching history for session: {}", userId, sessionId);
        
        ChatHistoryResponse response = chatService.getChatHistory(sessionId, userId, limit, offset);
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Error processing request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }
}
