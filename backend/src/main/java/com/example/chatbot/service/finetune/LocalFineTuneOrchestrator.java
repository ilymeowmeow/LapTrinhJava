package com.example.chatbot.service.finetune;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFineTuneOrchestrator {

    private final FineTuningDatasetService datasetService;
    
    private final AtomicReference<String> currentStatus = new AtomicReference<>("IDLE");

    public String startFineTuning() {
        if (!"IDLE".equals(currentStatus.get()) && !"FAILED".equals(currentStatus.get()) && !"COMPLETED".equals(currentStatus.get())) {
            return "Fine-tuning process is already running: " + currentStatus.get();
        }
        
        currentStatus.set("PREPARING_DATASET");
        
        CompletableFuture.runAsync(() -> {
            try {
                // 1. Generate Dataset
                String datasetPath = datasetService.generateDataset();
                log.info("Dataset prepared at {}", datasetPath);
                
                currentStatus.set("TRAINING");
                
                // 2. Start Python Subprocess
                ProcessBuilder pb = new ProcessBuilder("python", "train_local.py");
                pb.directory(new File("scripts/finetune"));
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[Unsloth Script] {}", line);
                    }
                }
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    currentStatus.set("COMPLETED");
                    log.info("Fine-tuning completed successfully. Model exported to GGUF format.");
                    
                    // In a production environment, we could automatically run:
                    // 'ollama create my-llama3-ft -f Modelfile' 
                    // and then update the Spring AI connection properties.
                } else {
                    currentStatus.set("FAILED");
                    log.error("Fine-tuning script failed with exit code {}", exitCode);
                }
                
            } catch (Exception e) {
                currentStatus.set("FAILED");
                log.error("Fine-tuning orchestration failed", e);
            }
        });
        
        return "Fine-tuning process started successfully.";
    }

    public String getStatus() {
        return currentStatus.get();
    }
}
