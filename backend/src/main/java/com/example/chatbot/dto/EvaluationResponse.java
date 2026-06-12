package com.example.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private String modelName;
    private double faithfulnessScore; // 0.0 - 1.0
    private double answerRelevanceScore; // 0.0 - 1.0
    private double contextPrecisionScore; // 0.0 - 1.0
    private double contextRecallScore; // 0.0 - 1.0
    private long latencyMs;
}
