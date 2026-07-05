package com.example.chatbot.dto;

import lombok.Data;

@Data
public class FineTuningRequest {
    private String modelName;
    private int loraR;
    private int loraAlpha;
    private int epochs;
    private String datasetName;
}
