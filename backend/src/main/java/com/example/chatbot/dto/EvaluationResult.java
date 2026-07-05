package com.example.chatbot.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class EvaluationResult {
    private RagResult ragResult;
    private BaseResult baseResult;

    @Data
    @Builder
    public static class RagResult {
        private String answer;
        private long timeTakenMs;
        private List<Map<String, Object>> sources;
    }

    @Data
    @Builder
    public static class BaseResult {
        private String answer;
        private long timeTakenMs;
    }
}
