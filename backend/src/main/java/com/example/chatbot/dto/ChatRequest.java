package com.example.chatbot.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String query;
    private String subject; // optional filtering
}
