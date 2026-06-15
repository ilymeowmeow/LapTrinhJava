package com.example.chatbot.service;

import com.example.chatbot.entity.ChatMessage;
import com.example.chatbot.entity.ChatSession;
import com.example.chatbot.repository.ChatMessageRepository;
import com.example.chatbot.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel; // Spring AI LLM Model (Gemini for RAG)
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String RAG_PROMPT_TEMPLATE = """
            Bạn là một trợ lý ảo giáo dục thông minh. Bạn chỉ được phép trả lời các câu hỏi dựa trên các tài liệu tham khảo được cung cấp bên dưới.
            Tuyệt đối không được bịa đặt (hallucinate) thông tin. Nếu trong tài liệu không có thông tin, hãy trả lời: "Tôi không tìm thấy thông tin này trong tài liệu môn học."
            
            Khi bạn sử dụng thông tin từ tài liệu, BẮT BUỘC phải kèm theo trích dẫn rõ ràng [Tên File - Nguồn].
            
            TÀI LIỆU THAM KHẢO:
            {context}
            
            CÂU HỎI CỦA NGƯỜI DÙNG:
            {question}
            """;

    private static final String FINE_TUNE_PROMPT_TEMPLATE = """
            Bạn là một trợ lý ảo giáo dục thông minh.
            Hãy sử dụng KIẾN THỨC NỘI TẠI (đã được fine-tune) của bạn để trả lời câu hỏi sau.
            Đừng trích dẫn tài liệu cụ thể nào nếu không chắc chắn.
            
            CÂU HỎI CỦA NGƯỜI DÙNG:
            {question}
            """;

    public String askQuestion(Long sessionId, String question, String mode, String subject, String localEndpoint) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseGet(() -> {
                    ChatSession newSession = ChatSession.builder().title("Auto-created Session").build();
                    return sessionRepository.save(newSession);
                });
        
        messageRepository.save(ChatMessage.builder()
                .session(session)
                .role("USER")
                .content(question)
                .build());

        String answer = "";

        if ("Fine-tuning Mode".equalsIgnoreCase(mode)) {
            // Proxies the request to local custom model (e.g. Ollama)
            if (localEndpoint != null && !localEndpoint.trim().isEmpty()) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    
                    // Simple Ollama standard payload
                    String payload = String.format("{\n" +
                            "  \"model\": \"llama3\",\n" +
                            "  \"prompt\": \"%s\",\n" +
                            "  \"stream\": false\n" +
                            "}", question.replace("\"", "\\\""));
                    
                    HttpEntity<String> request = new HttpEntity<>(payload, headers);
                    Map<String, Object> response = restTemplate.postForObject(localEndpoint, request, Map.class);
                    
                    if (response != null && response.containsKey("response")) {
                        answer = (String) response.get("response");
                    } else {
                        answer = "Nhận được phản hồi từ Local Model nhưng không thể đọc kết quả.";
                    }
                } catch (Exception e) {
                    answer = "Lỗi kết nối đến Local Model Endpoint (" + localEndpoint + "): " + e.getMessage();
                }
            } else {
                answer = "Vui lòng cấu hình URL cho Local Fine-Tuned Model trong phần Module Fine-Tuning trước khi sử dụng chế độ này!";
            }
        } else {
            // Default to RAG Mode
            List<Document> similarDocuments;
            try {
                SearchRequest searchRequest = SearchRequest.query(question).withTopK(5);
                if (subject != null && !subject.trim().isEmpty()) {
                    searchRequest = searchRequest.withFilterExpression("subject == '" + subject + "'");
                }
                similarDocuments = vectorStore.similaritySearch(searchRequest);
            } catch (Exception e) {
                similarDocuments = List.of();
                System.err.println("Vector DB not ready: " + e.getMessage());
            }

            String context = similarDocuments.stream()
                    .map(doc -> "Nội dung: " + doc.getContent() + " \n [Nguồn: " + doc.getMetadata().getOrDefault("filename", "Unknown") + "]")
                    .collect(Collectors.joining("\n\n"));

            PromptTemplate promptTemplate = new PromptTemplate(RAG_PROMPT_TEMPLATE);
            Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));
            
            try {
                answer = chatModel.call(prompt).getResult().getOutput().getContent();
            } catch (Exception e) {
                answer = "Lỗi kết nối Gemini API (RAG): " + e.getMessage();
            }
        }

        messageRepository.save(ChatMessage.builder()
                .session(session)
                .role("BOT")
                .content(answer)
                .build());

        return answer;
    }
}
