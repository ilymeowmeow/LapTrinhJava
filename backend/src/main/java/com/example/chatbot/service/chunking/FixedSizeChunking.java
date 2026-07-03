package com.example.chatbot.service.chunking;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FixedSizeChunking implements ChunkingStrategy {
    
    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;

    @Override
    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int length = text.length();
        for (int i = 0; i < length; i += (CHUNK_SIZE - OVERLAP)) {
            int end = Math.min(length, i + CHUNK_SIZE);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }
}
