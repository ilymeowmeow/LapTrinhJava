package com.example.chatbot.controller;

import com.example.chatbot.dto.FineTuningRequest;
import com.example.chatbot.service.FineTuningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finetuning")
@CrossOrigin(origins = "*")
public class FineTuningController {

    private final FineTuningService fineTuningService;

    public FineTuningController(FineTuningService fineTuningService) {
        this.fineTuningService = fineTuningService;
    }

    @PostMapping("/generate-script")
    public ResponseEntity<Map<String, String>> generateScript(@RequestBody FineTuningRequest request) {
        String script = fineTuningService.generatePythonScript(
                request.getModelName(),
                request.getLoraR(),
                request.getLoraAlpha(),
                request.getEpochs(),
                request.getDatasetName()
        );
        return ResponseEntity.ok(Map.of("script", script));
    }
}
