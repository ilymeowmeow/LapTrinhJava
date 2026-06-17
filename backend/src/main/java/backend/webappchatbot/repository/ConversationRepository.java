package backend.webappchatbot.repository;

import backend.webappchatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByStudentId(Long studentId);

    List<Conversation> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Conversation> findByCourseId(Long courseId);

    List<Conversation> findByMethod(String method);
}

