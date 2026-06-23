package com.example.chatbot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorStorageService {

    private final VectorStore vectorStore;

    public VectorStorageService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveChunks(List<String> chunks, Long sourceDocumentId) {
        // Convert plain string chunks to Spring AI Document objects with metadata
        List<Document> documents = chunks.stream()
                .map(chunk -> new Document(chunk, Map.of("sourceDocumentId", sourceDocumentId)))
                .collect(Collectors.toList());

        // Save to PostgreSQL via pgvector
        vectorStore.add(documents);
        System.out.println("Saved " + chunks.size() + " chunks to pgvector database.");
    }
}
