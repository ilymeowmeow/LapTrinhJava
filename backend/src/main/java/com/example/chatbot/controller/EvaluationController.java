package com.example.chatbot.controller;

import com.example.chatbot.dto.EvaluationResult;
import com.example.chatbot.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/compare")
    public ResponseEntity<EvaluationResult> compareModels(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        String localEndpoint = payload.get("localEndpoint");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        EvaluationResult result = evaluationService.compareModels(query, localEndpoint);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chunking")
    public ResponseEntity<Map<String, Object>> benchmarkChunking(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> result = evaluationService.benchmarkChunking(text);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/embedding")
    public ResponseEntity<Map<String, Object>> benchmarkEmbedding(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> result = evaluationService.benchmarkEmbedding(text);
        return ResponseEntity.ok(result);
    }
}
