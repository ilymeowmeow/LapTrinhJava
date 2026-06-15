package com.example.chatbot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Primary
@Service
public class GeminiNativeEmbeddingModel implements EmbeddingModel {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public float[] embed(String text) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-2:embedContent?key=" + apiKey;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "model", "models/gemini-embedding-2",
            "content", Map.of("parts", List.of(Map.of("text", text)))
        );
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> embedding = (Map<String, Object>) response.getBody().get("embedding");
            List<Number> values = (List<Number>) embedding.get("values");
            float[] floatValues = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                floatValues[i] = values.get(i).floatValue();
            }
            
            // Truncate to 768 dimensions to match vector_store and HNSW limits
            if (floatValues.length > 768) {
                floatValues = java.util.Arrays.copyOf(floatValues, 768);
            }
            
            return floatValues;
        } catch (Exception e) {
            System.err.println("Gemini Embedding API Error (Using mock embedding): " + e.getMessage());
            // Return a mock embedding array of 768 dimensions for Demo Mode
            return new float[768];
        }
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getContent());
    }

    @Override
    public EmbeddingResponse call(org.springframework.ai.embedding.EmbeddingRequest request) {
        List<String> texts = request.getInstructions();
        List<Embedding> embeddings = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            embeddings.add(new Embedding(embed(texts.get(i)), i));
        }
        return new EmbeddingResponse(embeddings);
    }

    @Override
    public int dimensions() {
        return 768;
    }
}
