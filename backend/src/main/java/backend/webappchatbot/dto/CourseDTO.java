package backend.webappchatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private String instructor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

