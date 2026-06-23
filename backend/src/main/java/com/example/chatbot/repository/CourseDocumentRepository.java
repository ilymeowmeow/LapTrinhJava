package com.example.chatbot.repository;

import com.example.chatbot.domain.CourseDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDocumentRepository extends JpaRepository<CourseDocument, Long> {
    List<CourseDocument> findBySubject(String subject);
}
