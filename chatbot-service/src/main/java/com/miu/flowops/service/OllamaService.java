package com.miu.flowops.service;

import com.miu.flowops.config.OllamaConfig;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OllamaService {
    
    private final WebClient webClient;
    private final OllamaConfig config;
    private static final String FALLBACK_MESSAGE = "I apologize, but I'm temporarily unavailable. Please try again in a moment.";
    
    public OllamaService(OllamaConfig config) {
        this.config = config;
        
        // Configure HttpClient with timeouts
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getTimeout())
                .responseTimeout(Duration.ofMillis(config.getTimeout()))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(config.getTimeout(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(config.getTimeout(), TimeUnit.MILLISECONDS)));
        
        this.webClient = WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
    
    public String generateResponse(String prompt) {
        try {
            return callOllama(prompt)
                    .retryWhen(Retry.fixedDelay(config.getRetry().getMaxAttempts(), 
                            Duration.ofMillis(config.getRetry().getDelay()))
                            .doBeforeRetry(signal -> 
                                log.warn("Retrying Ollama request, attempt: {}", signal.totalRetries() + 1)))
                    .onErrorResume(e -> {
                        log.error("Ollama service failed after retries: {}", e.getMessage());
                        return Mono.just(FALLBACK_MESSAGE);
                    })
                    .block(Duration.ofMillis(config.getTimeout()));
        } catch (Exception e) {
            log.error("Unexpected error calling Ollama: {}", e.getMessage());
            return FALLBACK_MESSAGE;
        }
    }
    
    private Mono<String> callOllama(String prompt) {
        Map<String, Object> request = Map.of(
                "model", config.getModel(),
                "prompt", prompt,
                "stream", false,
                "options", Map.of("temperature", 0.7)
        );
        
        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object responseText = response.get("response");
                    if (responseText != null) {
                        return responseText.toString();
                    }
                    log.warn("Empty response from Ollama");
                    return FALLBACK_MESSAGE;
                })
                .doOnError(WebClientException.class, e -> 
                    log.error("WebClient error calling Ollama: {}", e.getMessage()));
    }
}
