package backend.webappchatbot.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @GetMapping
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("message", "WebAppChatbot service is running");
        health.put("version", "1.0.0");
        return health;
    }

    @GetMapping("/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("appName", "WebAppChatbot");
        info.put("description", "Chatbot for Vietnamese student Q&A based on course materials");
        info.put("features", "RAG & Fine-tuning comparison research");
        return info;
    }
}

