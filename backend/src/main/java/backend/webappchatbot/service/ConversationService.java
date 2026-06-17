package backend.webappchatbot.service;

import backend.webappchatbot.model.Conversation;
import backend.webappchatbot.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation createConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public Optional<Conversation> getConversationById(Long id) {
        return conversationRepository.findById(id);
    }

    public List<Conversation> getConversationsByStudentId(Long studentId) {
        return conversationRepository.findByStudentId(studentId);
    }

    public List<Conversation> getConversationsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return conversationRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public List<Conversation> getConversationsByCourseId(Long courseId) {
        return conversationRepository.findByCourseId(courseId);
    }

    public List<Conversation> getConversationsByMethod(String method) {
        return conversationRepository.findByMethod(method);
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Conversation updateConversation(Long id, Conversation conversationDetails) {
        Optional<Conversation> conversation = conversationRepository.findById(id);
        if (conversation.isPresent()) {
            Conversation existingConversation = conversation.get();
            if (conversationDetails.getTitle() != null) {
                existingConversation.setTitle(conversationDetails.getTitle());
            }
            if (conversationDetails.getSummary() != null) {
                existingConversation.setSummary(conversationDetails.getSummary());
            }
            return conversationRepository.save(existingConversation);
        }
        return null;
    }

    public boolean deleteConversation(Long id) {
        if (conversationRepository.existsById(id)) {
            conversationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

