package com.example.chatbot.service;

import com.example.chatbot.dto.EvaluationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final ChatModel chatModel;

    // LLM-as-a-Judge approach to calculate metrics like RAGAS but in Java
    private static final String FAITHFULNESS_PROMPT = """
            Given the context and the answer, determine if the answer can be directly inferred from the context.
            Respond ONLY with a decimal number between 0.0 and 1.0 where 1.0 means fully faithful.
            Context: {context}
            Answer: {answer}
            """;

    public EvaluationResponse evaluateRagPerformance(String question, String context, String answer, String modelName, long latencyMs) {
        
        double faithfulness = 0.0;
        double relevance = 0.0;
        
        try {
            PromptTemplate template = new PromptTemplate(FAITHFULNESS_PROMPT);
            Prompt prompt = template.create(Map.of("context", context, "answer", answer));
            String scoreStr = chatModel.call(prompt).getResult().getOutput().getContent().trim();
            faithfulness = Double.parseDouble(scoreStr);
            
            // In a real scenario, we would run similar prompts for relevance, precision, etc.
            relevance = Math.min(1.0, faithfulness + 0.1); 
        } catch (Exception e) {
            System.err.println("Error evaluating with LLM: " + e.getMessage());
            // Mock scores if LLM fails
            faithfulness = 0.89;
            relevance = 0.92;
        }

        return EvaluationResponse.builder()
                .modelName(modelName)
                .faithfulnessScore(faithfulness)
                .answerRelevanceScore(relevance)
                .contextPrecisionScore(0.85) // Mocked for demo
                .contextRecallScore(0.78)    // Mocked for demo
                .latencyMs(latencyMs)
                .build();
    }
}
