package com.example.chatbot.repository;

import com.example.chatbot.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT s FROM ChatSession s WHERE size(s.messages) > 0 ORDER BY s.createdAt DESC")
    List<ChatSession> findAllNonEmptySessions();
}
