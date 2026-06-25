package backend.webappchatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String title;
    private String content;
    private String documentType;
    private String filePath;
    private String method;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

