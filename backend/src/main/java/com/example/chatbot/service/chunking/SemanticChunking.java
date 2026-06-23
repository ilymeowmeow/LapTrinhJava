package com.example.chatbot.service.chunking;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SemanticChunking implements ChunkingStrategy {
    
    @Override
    public List<String> chunkText(String text) {
        // A simple semantic chunking based on double newlines (paragraphs)
        // In a real RAG system, this would use NLP embeddings to find topic boundaries
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        String[] paragraphs = text.split("\\n\\s*\\n");
        return Arrays.asList(paragraphs);
    }
}
