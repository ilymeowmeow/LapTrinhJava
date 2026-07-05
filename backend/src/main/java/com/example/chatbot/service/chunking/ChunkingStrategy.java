package com.example.chatbot.service.chunking;

import java.util.List;

public interface ChunkingStrategy {
    List<String> chunkText(String text);
}
