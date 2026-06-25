package backend.webappchatbot.controller;

import backend.webappchatbot.model.Document;
import backend.webappchatbot.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        Document createdDocument = documentService.createDocument(document);
        return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Document>> getDocumentsByCourseId(@PathVariable Long courseId) {
        List<Document> documents = documentService.getDocumentsByCourseId(courseId);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}/method/{method}")
    public ResponseEntity<List<Document>> getDocumentsByCourseIdAndMethod(
            @PathVariable Long courseId,
            @PathVariable String method) {
        List<Document> documents = documentService.getDocumentsByCourseIdAndMethod(courseId, method);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<List<Document>> searchDocumentsByTitle(@PathVariable String title) {
        List<Document> documents = documentService.searchDocumentsByTitle(title);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document documentDetails) {
        Document updatedDocument = documentService.updateDocument(id, documentDetails);
        if (updatedDocument != null) {
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        if (documentService.deleteDocument(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.notFound().build();
    }
}

