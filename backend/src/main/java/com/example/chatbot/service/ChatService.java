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

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel; // Spring AI LLM Model (Gemini for RAG)
    private ChatModel fineTunedChatModel; // Manual instantiation for Groq/Llama 3
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Value("${spring.groq.api-key}")
    private String groqApiKey;

    @Value("${spring.groq.base-url:https://api.groq.com/openai}")
    private String groqBaseUrl;

    @Value("${spring.groq.model:llama-3.1-8b-instant}")
    private String groqModel;

    @PostConstruct
    public void initGroqModel() {
        try {
            OpenAiApi api = new OpenAiApi(groqBaseUrl, groqApiKey);
            this.fineTunedChatModel = new OpenAiChatModel(api, OpenAiChatOptions.builder().withModel(groqModel).build());
        } catch (Exception e) {
            System.err.println("Warning: Failed to initialize Groq Llama 3 model: " + e.getMessage());
        }
    }

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

    public String askQuestion(Long sessionId, String question, String mode) {
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

        Prompt prompt;
        if ("Fine-tuning Mode".equalsIgnoreCase(mode)) {
            PromptTemplate promptTemplate = new PromptTemplate(FINE_TUNE_PROMPT_TEMPLATE);
            prompt = promptTemplate.create(Map.of("question", question));
        } else {
            // Default to RAG Mode
            List<Document> similarDocuments;
            try {
                similarDocuments = vectorStore.similaritySearch(
                        SearchRequest.query(question).withTopK(5)
                );
            } catch (Exception e) {
                similarDocuments = List.of();
                System.err.println("Vector DB not ready: " + e.getMessage());
            }

            String context = similarDocuments.stream()
                    .map(doc -> "Nội dung: " + doc.getContent() + " \n [Nguồn: " + doc.getMetadata().getOrDefault("sourceDocumentId", "Unknown") + "]")
                    .collect(Collectors.joining("\n\n"));

            PromptTemplate promptTemplate = new PromptTemplate(RAG_PROMPT_TEMPLATE);
            prompt = promptTemplate.create(Map.of("context", context, "question", question));
        }

        String answer;
        try {
            if ("Fine-tuning Mode".equalsIgnoreCase(mode) && fineTunedChatModel != null) {
                answer = fineTunedChatModel.call(prompt).getResult().getOutput().getContent();
            } else {
                answer = chatModel.call(prompt).getResult().getOutput().getContent();
            }
        } catch (Exception e) {
            System.err.println("Error calling LLM (possibly missing API Key): " + e.getMessage());
            answer = "Hệ thống đang hoạt động ở chế độ Demo (" + mode + "): Đây là câu trả lời được sinh tự động do thiếu API Key hoặc lỗi kết nối LLM.";
        }

        messageRepository.save(ChatMessage.builder()
                .session(session)
                .role("BOT")
                .content(answer)
                .build());

        return answer;
    }
}
