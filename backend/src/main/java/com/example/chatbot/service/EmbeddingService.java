package com.example.chatbot.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    /**
     * In a real implementation, this would use Spring AI's EmbeddingModel 
     * or RestTemplate to call OpenAI/HuggingFace endpoints.
     */
    public List<Double> getEmbedding(String text, String modelName) {
        // Placeholder for calling multilingual-e5-base, text-embedding-3-small, etc.
        System.out.println("Generating embedding for text using model: " + modelName);
        
        // Mock returning a dummy vector of 3 dimensions for pgvector demonstration
        return List.of(0.1, 0.2, 0.3);
    }
    
    public List<List<Double>> getEmbeddings(List<String> chunks, String modelName) {
        return chunks.stream()
                .map(chunk -> getEmbedding(chunk, modelName))
                .collect(Collectors.toList());
    }
}
