package backend.webappchatbot.service;

import backend.webappchatbot.model.Document;
import backend.webappchatbot.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Document> getDocumentsByCourseId(Long courseId) {
        return documentRepository.findByCourseId(courseId);
    }

    public List<Document> getDocumentsByCourseIdAndMethod(Long courseId, String method) {
        return documentRepository.findByCourseIdAndMethod(courseId, method);
    }

    public List<Document> searchDocumentsByTitle(String title) {
        return documentRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document updateDocument(Long id, Document documentDetails) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            Document existingDocument = document.get();
            if (documentDetails.getTitle() != null) {
                existingDocument.setTitle(documentDetails.getTitle());
            }
            if (documentDetails.getContent() != null) {
                existingDocument.setContent(documentDetails.getContent());
            }
            if (documentDetails.getDocumentType() != null) {
                existingDocument.setDocumentType(documentDetails.getDocumentType());
            }
            if (documentDetails.getMethod() != null) {
                existingDocument.setMethod(documentDetails.getMethod());
            }
            return documentRepository.save(existingDocument);
        }
        return null;
    }

    public boolean deleteDocument(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

