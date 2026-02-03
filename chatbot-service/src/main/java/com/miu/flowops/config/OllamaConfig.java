package com.miu.flowops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {
    
    private String baseUrl = "http://localhost:11434";
    private String model = "llama3.2:1b";
    private int timeout = 30000;
    private Retry retry = new Retry();
    
    @Data
    public static class Retry {
        private int maxAttempts = 2;
        private long delay = 1000;
    }
}
