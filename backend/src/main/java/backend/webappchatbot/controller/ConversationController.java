package backend.webappchatbot.controller;

import backend.webappchatbot.model.Conversation;
import backend.webappchatbot.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/conversations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        Conversation createdConversation = conversationService.createConversation(conversation);
        return new ResponseEntity<>(createdConversation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable Long id) {
        Optional<Conversation> conversation = conversationService.getConversationById(id);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Conversation>> getConversationsByStudentId(@PathVariable Long studentId) {
        List<Conversation> conversations = conversationService.getConversationsByStudentId(studentId);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<Conversation>> getConversationsByStudentIdAndCourseId(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<Conversation> conversations = conversationService.getConversationsByStudentIdAndCourseId(studentId, courseId);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Conversation>> getConversationsByCourseId(@PathVariable Long courseId) {
        List<Conversation> conversations = conversationService.getConversationsByCourseId(courseId);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<List<Conversation>> getConversationsByMethod(@PathVariable String method) {
        List<Conversation> conversations = conversationService.getConversationsByMethod(method);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        List<Conversation> conversations = conversationService.getAllConversations();
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conversation> updateConversation(@PathVariable Long id, @RequestBody Conversation conversationDetails) {
        Conversation updatedConversation = conversationService.updateConversation(id, conversationDetails);
        if (updatedConversation != null) {
            return new ResponseEntity<>(updatedConversation, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        if (conversationService.deleteConversation(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.notFound().build();
    }
}

