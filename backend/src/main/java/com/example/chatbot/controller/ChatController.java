package com.example.chatbot.controller;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.entity.ChatSession;
import com.example.chatbot.repository.ChatMessageRepository;
import com.example.chatbot.repository.ChatSessionRepository;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/session")
    public ResponseEntity<ChatSession> createSession(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "New Conversation");
        ChatSession session = ChatSession.builder().title(title).build();
        return ResponseEntity.ok(chatSessionRepository.save(session));
    }

    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    @PostMapping("/ask/{sessionId}")
    public ResponseEntity<Map<String, String>> askQuestion(@PathVariable Long sessionId, @RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        String mode = payload.getOrDefault("mode", "RAG Mode");
        
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String answer = chatService.askQuestion(sessionId, question, mode);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
