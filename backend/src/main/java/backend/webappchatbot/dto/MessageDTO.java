package backend.webappchatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private String role;
    private String content;
    private String sourceDocuments;
    private String confidence;
    private LocalDateTime createdAt;
}

