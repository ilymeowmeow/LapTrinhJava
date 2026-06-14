package com.example.chatbot.controller;

import com.example.chatbot.service.finetune.LocalFineTuneOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finetune")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FineTuningController {

    private final LocalFineTuneOrchestrator orchestrator;

    @PostMapping("/start")
    public ResponseEntity<?> startFineTuning() {
        String msg = orchestrator.startFineTuning();
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(Map.of("status", orchestrator.getStatus()));
    }
}
