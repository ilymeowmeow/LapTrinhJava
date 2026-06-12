package com.example.chatbot.controller;

import com.example.chatbot.domain.CourseDocument;
import com.example.chatbot.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*") // For development, allow Next.js
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<CourseDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "chapter", required = false) String chapter) {
        try {
            CourseDocument saved = documentService.uploadAndIndex(file, subject, chapter);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CourseDocument>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
}
