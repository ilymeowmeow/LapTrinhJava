package com.example.chatbot.controller;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.entity.ChatSession;
import com.example.chatbot.entity.User;
import com.example.chatbot.repository.ChatMessageRepository;
import com.example.chatbot.repository.ChatSessionRepository;
import com.example.chatbot.repository.UserRepository;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        try {
            return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> payload) {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String title = payload.getOrDefault("title", "New Conversation");
        ChatSession session = ChatSession.builder().title(title).user(user).build();
        return ResponseEntity.ok(chatSessionRepository.save(session));
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions() {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(chatSessionRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    @PostMapping("/ask/{sessionId}")
    public ResponseEntity<?> askQuestion(@PathVariable Long sessionId, @RequestBody Map<String, String> payload) {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String question = payload.get("question");
        String mode = payload.getOrDefault("mode", "RAG Mode");
        String subject = payload.get("subject");
        String localEndpoint = payload.get("localModelEndpoint");
        
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String answer = chatService.askQuestion(sessionId, question, mode, subject, localEndpoint);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @PostMapping("")
    public ResponseEntity<?> askQuestionGeneric(@RequestBody Map<String, String> payload) {
        Long userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Create a generic session if it doesn't exist for this user
        // Note: For simplicity, creating a new session per generic call.
        // In a real app, the frontend should create a session first.
        ChatSession session = ChatSession.builder().title("Generic Session").user(user).build();
        session = chatSessionRepository.save(session);
        
        String question = payload.get("query"); // Matches frontend "query"
        String mode = payload.getOrDefault("mode", "rag"); // Matches frontend "rag" or "finetune"
        String subject = payload.get("subject");
        
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String mappedMode = "finetune".equalsIgnoreCase(mode) ? "Fine-tuning Mode" : "RAG Mode";
        String localEndpoint = payload.get("localModelEndpoint");
        String answer = chatService.askQuestion(session.getId(), question, mappedMode, subject, localEndpoint);
        
        return ResponseEntity.ok(Map.of("answer", answer, "sessionId", session.getId()));
    }
}
