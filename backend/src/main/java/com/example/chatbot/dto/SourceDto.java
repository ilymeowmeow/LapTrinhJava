package com.example.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceDto {
    private String filename;
    private String chapter;
    private String content; // text snippet
}
