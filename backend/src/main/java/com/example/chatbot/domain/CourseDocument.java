package com.example.chatbot.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_documents")
@Data
public class CourseDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String filename;
    private String subject;
    private String chapter;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    
    private String status; // e.g. "INDEXED", "FAILED"
}
