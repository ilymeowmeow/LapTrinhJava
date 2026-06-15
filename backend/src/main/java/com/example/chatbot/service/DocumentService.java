package com.example.chatbot.service;

import com.example.chatbot.domain.CourseDocument;
import com.example.chatbot.repository.CourseDocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {

    private final VectorStore vectorStore;
    private final CourseDocumentRepository repository;

    public DocumentService(VectorStore vectorStore, CourseDocumentRepository repository) {
        this.vectorStore = vectorStore;
        this.repository = repository;
    }

    public CourseDocument uploadAndIndex(MultipartFile file, String subject, String chapter) throws Exception {
        // 1. Save metadata to DB
        CourseDocument docMeta = new CourseDocument();
        docMeta.setFilename(file.getOriginalFilename());
        docMeta.setSubject(subject);
        docMeta.setChapter(chapter);
        docMeta.setUploadDate(LocalDateTime.now());
        docMeta.setStatus("PROCESSING");
        docMeta = repository.save(docMeta);

        try {
            // 2. Read document using Tika
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> documents = reader.get();

            // 3. Add metadata to each document
            for (Document doc : documents) {
                Map<String, Object> metadata = doc.getMetadata();
                metadata.put("doc_id", docMeta.getId());
                metadata.put("subject", subject != null ? subject : "Unknown");
                metadata.put("chapter", chapter != null ? chapter : "Unknown");
                metadata.put("filename", file.getOriginalFilename());
            }

            // 4. Chunking (Token-based Text Splitter)
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunkedDocuments = splitter.apply(documents);

            // 5. Store to Vector Database
            vectorStore.add(chunkedDocuments);

            // 6. Update Status
            docMeta.setStatus("INDEXED");
            repository.save(docMeta);

            return docMeta;
        } catch (Exception e) {
            e.printStackTrace();
            docMeta.setStatus("FAILED");
            repository.save(docMeta);
            throw new RuntimeException("Failed to process document", e);
        }
    }

    public List<CourseDocument> getAllDocuments() {
        return repository.findAll();
    }
}
