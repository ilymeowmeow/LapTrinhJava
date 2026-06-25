package backend.webappchatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String title;
    private String method;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

