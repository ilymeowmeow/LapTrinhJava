package com.example.chatbot.service.chunking;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HierarchicalChunking implements ChunkingStrategy {

    @Override
    public List<String> chunkText(String text) {
        // Splitting into hierarchical structure (Paragraphs -> Sentences)
        // Returning flattened sentences with paragraph context for simplicity in this demo
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) return chunks;

        String[] paragraphs = text.split("\\n\\s*\\n");
        for (String p : paragraphs) {
            String[] sentences = p.split("(?<=[.!?])\\s+");
            for (String s : sentences) {
                if (!s.trim().isEmpty()) {
                    chunks.add(s.trim());
                }
            }
        }
        return chunks;
    }
}
