package backend.webappchatbot.service;

import backend.webappchatbot.model.Message;
import backend.webappchatbot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    public List<Message> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    public List<Message> getMessagesByConversationIdAndRole(Long conversationId, String role) {
        return messageRepository.findByConversationIdAndRole(conversationId, role);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message updateMessage(Long id, Message messageDetails) {
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent()) {
            Message existingMessage = message.get();
            if (messageDetails.getContent() != null) {
                existingMessage.setContent(messageDetails.getContent());
            }
            if (messageDetails.getSourceDocuments() != null) {
                existingMessage.setSourceDocuments(messageDetails.getSourceDocuments());
            }
            if (messageDetails.getConfidence() != null) {
                existingMessage.setConfidence(messageDetails.getConfidence());
            }
            return messageRepository.save(existingMessage);
        }
        return null;
    }

    public boolean deleteMessage(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

