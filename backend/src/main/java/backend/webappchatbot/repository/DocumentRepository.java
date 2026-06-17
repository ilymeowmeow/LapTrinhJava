package backend.webappchatbot.repository;

import backend.webappchatbot.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCourseId(Long courseId);

    List<Document> findByCourseIdAndMethod(Long courseId, String method);

    List<Document> findByTitleContainingIgnoreCase(String title);
}

