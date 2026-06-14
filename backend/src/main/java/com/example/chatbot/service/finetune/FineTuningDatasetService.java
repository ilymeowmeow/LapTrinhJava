package com.example.chatbot.service.finetune;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FineTuningDatasetService {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    /**
     * Extracts chat history and formats it into Alpaca JSONL format for Fine-Tuning.
     * Expected format: {"instruction": "...", "input": "...", "output": "..."}
     */
    public String generateDataset() {
        String outputPath = "scripts/finetune/dataset.jsonl";
        File dir = new File("scripts/finetune");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<ChatMessage> messages = chatMessageRepository.findAll();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            ChatMessage lastUserMessage = null;
            
            for (ChatMessage msg : messages) {
                if ("USER".equalsIgnoreCase(msg.getRole())) {
                    lastUserMessage = msg;
                } else if ("BOT".equalsIgnoreCase(msg.getRole()) && lastUserMessage != null) {
                    Map<String, String> jsonlRecord = new HashMap<>();
                    jsonlRecord.put("instruction", "Bạn là một trợ lý ảo thông minh. Hãy trả lời câu hỏi sau.");
                    jsonlRecord.put("input", lastUserMessage.getContent());
                    jsonlRecord.put("output", msg.getContent());
                    
                    writer.write(objectMapper.writeValueAsString(jsonlRecord));
                    writer.newLine();
                    
                    lastUserMessage = null; // reset for next pair
                }
            }
            log.info("Dataset generated successfully at {}", outputPath);
            return outputPath;
        } catch (IOException e) {
            log.error("Failed to generate dataset", e);
            throw new RuntimeException("Failed to generate dataset", e);
        }
    }
}
