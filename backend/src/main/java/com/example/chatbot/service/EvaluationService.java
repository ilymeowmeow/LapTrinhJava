package com.example.chatbot.service;

import com.example.chatbot.dto.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    private static final String RAG_PROMPT_TEMPLATE = """
            Bạn là một trợ lý ảo giáo dục thông minh. Bạn chỉ được phép trả lời dựa trên các tài liệu tham khảo bên dưới.
            
            TÀI LIỆU THAM KHẢO:
            {context}
            
            CÂU HỎI CỦA NGƯỜI DÙNG:
            {question}
            """;

    private static final String BASE_PROMPT_TEMPLATE = """
            Bạn là một trợ lý ảo giáo dục thông minh.
            Hãy sử dụng kiến thức chung để trả lời câu hỏi sau.
            
            CÂU HỎI CỦA NGƯỜI DÙNG:
            {question}
            """;

    public EvaluationResult compareModels(String query, String localEndpoint) {
        // 1. Test RAG Model
        long ragStartTime = System.currentTimeMillis();
        
        // Search vector db
        List<Document> similarDocuments = List.of();
        try {
            similarDocuments = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(3));
        } catch (Exception e) {
            System.err.println("Vector DB not ready: " + e.getMessage());
        }

        String context = similarDocuments.stream()
                .map(doc -> "Nội dung: " + doc.getContent())
                .collect(Collectors.joining("\n\n"));

        PromptTemplate ragPromptTemplate = new PromptTemplate(RAG_PROMPT_TEMPLATE);
        Prompt ragPrompt = ragPromptTemplate.create(Map.of("context", context, "question", query));
        
        String ragAnswer = "";
        try {
            ragAnswer = chatModel.call(ragPrompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            ragAnswer = "Error calling Gemini API: " + e.getMessage();
        }
        long ragEndTime = System.currentTimeMillis();

        List<Map<String, Object>> sources = similarDocuments.stream().map(doc -> {
            Map<String, Object> source = new HashMap<>();
            source.put("filename", doc.getMetadata().getOrDefault("filename", "Unknown"));
            return source;
        }).collect(Collectors.toList());

        EvaluationResult.RagResult ragResult = EvaluationResult.RagResult.builder()
                .answer(ragAnswer)
                .timeTakenMs(ragEndTime - ragStartTime)
                .sources(sources)
                .build();

        // 2. Test Fine-tuned Model (Local Endpoint)
        long baseStartTime = System.currentTimeMillis();
        String baseAnswer = "";
        
        if (localEndpoint != null && !localEndpoint.trim().isEmpty()) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                
                String payload = String.format("{\n" +
                        "  \"model\": \"llama3\",\n" +
                        "  \"prompt\": \"%s\",\n" +
                        "  \"stream\": false\n" +
                        "}", query.replace("\"", "\\\""));
                
                org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(payload, headers);
                Map<String, Object> response = restTemplate.postForObject(localEndpoint, request, Map.class);
                
                if (response != null && response.containsKey("response")) {
                    baseAnswer = (String) response.get("response");
                } else {
                    baseAnswer = "Lỗi đọc dữ liệu từ Local Model.";
                }
            } catch (Exception e) {
                baseAnswer = "Lỗi kết nối Local Endpoint (" + localEndpoint + "): " + e.getMessage();
            }
        } else {
            baseAnswer = "Chưa cấu hình Local Endpoint. Vui lòng điền ở màn hình RBL.";
        }
        long baseEndTime = System.currentTimeMillis();

        EvaluationResult.BaseResult baseResult = EvaluationResult.BaseResult.builder()
                .answer(baseAnswer)
                .timeTakenMs(baseEndTime - baseStartTime)
                .build();

        return EvaluationResult.builder()
                .ragResult(ragResult)
                .baseResult(baseResult)
                .build();
    }

    public Map<String, Object> benchmarkChunking(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Map.of("error", "Empty text");
        }

        Document doc = new Document(text);

        // 1. TokenTextSplitter (Default in Spring AI)
        long startToken = System.currentTimeMillis();
        TokenTextSplitter tokenSplitter = new TokenTextSplitter(50, 10, 5, 1000, true);
        List<Document> tokenChunks = tokenSplitter.apply(List.of(doc));
        long endToken = System.currentTimeMillis();

        // 2. Sentence Splitter (Chia theo câu hoặc cụm từ ngắn)
        long startSentence = System.currentTimeMillis();
        String[] sentences = text.split("(?<=[.,!?])\\s+");
        List<String> sentenceChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > 50) {
                if (currentChunk.length() > 0) {
                    sentenceChunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
            }
            currentChunk.append(sentence).append(" ");
        }
        if (currentChunk.length() > 0) {
            sentenceChunks.add(currentChunk.toString().trim());
        }
        long endSentence = System.currentTimeMillis();

        // 3. Paragraph Splitter (Chia theo đoạn)
        long startParagraph = System.currentTimeMillis();
        String[] paragraphs = text.split("\\n+");
        List<String> paragraphChunks = new ArrayList<>();
        StringBuilder currentParaChunk = new StringBuilder();
        for (String para : paragraphs) {
            if (currentParaChunk.length() + para.length() > 50) {
                if (currentParaChunk.length() > 0) {
                    paragraphChunks.add(currentParaChunk.toString().trim());
                    currentParaChunk = new StringBuilder();
                }
            }
            currentParaChunk.append(para).append("\n");
        }
        if (currentParaChunk.length() > 0) {
            paragraphChunks.add(currentParaChunk.toString().trim());
        }
        long endParagraph = System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        
        result.put("tokenSplitter", Map.of(
            "name", "TokenTextSplitter (Spring AI)",
            "timeMs", endToken - startToken,
            "chunkCount", tokenChunks.size(),
            "chunks", tokenChunks.stream().map(Document::getContent).collect(Collectors.toList())
        ));

        result.put("sentenceSplitter", Map.of(
            "name", "Sentence Splitter",
            "timeMs", endSentence - startSentence,
            "chunkCount", sentenceChunks.size(),
            "chunks", sentenceChunks
        ));

        result.put("paragraphSplitter", Map.of(
            "name", "Paragraph Splitter",
            "timeMs", endParagraph - startParagraph,
            "chunkCount", paragraphChunks.size(),
            "chunks", paragraphChunks
        ));

        return result;
    }

    @org.springframework.beans.factory.annotation.Value("${spring.ai.openai.api-key}")
    private String geminiApiKey;

    private org.springframework.ai.transformers.TransformersEmbeddingModel localEmbeddingModel;
    private final java.util.concurrent.CompletableFuture<org.springframework.ai.transformers.TransformersEmbeddingModel> localModelFuture = new java.util.concurrent.CompletableFuture<>();

    @jakarta.annotation.PostConstruct
    public void initLocalModel() {
        new Thread(() -> {
            try {
                org.springframework.ai.transformers.TransformersEmbeddingModel model = new org.springframework.ai.transformers.TransformersEmbeddingModel();
                model.afterPropertiesSet();
                localModelFuture.complete(model);
            } catch (Exception e) {
                localModelFuture.completeExceptionally(e);
            }
        }).start();
    }

    public Map<String, Object> benchmarkEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Map.of("error", "Empty text");
        }

        Map<String, Object> result = new HashMap<>();

        // 1. Local Embedding Model (ONNX)
        long localStart = System.currentTimeMillis();
        int localDims = 0;
        try {
            if (localEmbeddingModel == null) {
                localEmbeddingModel = localModelFuture.get(); // blocks until ready
            }
            float[] emb = localEmbeddingModel.embed(text);
            localDims = emb.length;
        } catch (Exception e) {
            System.err.println("Local Embedding failed: " + e.getMessage());
        }
        long localEnd = System.currentTimeMillis();

        result.put("localModel", Map.of(
            "name", "all-MiniLM-L6-v2",
            "provider", "HuggingFace/ONNX (Local CPU)",
            "dimensions", localDims > 0 ? localDims : "Error",
            "timeMs", localEnd - localStart
        ));

        // 2. Gemini Cloud Embedding
        long cloudStart = System.currentTimeMillis();
        int cloudDims = 0;
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String url = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent?key=" + geminiApiKey;
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            String payload = "{\"model\": \"models/text-embedding-004\", \"content\": {\"parts\": [{\"text\": \"" + text.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}}";
            
            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(payload, headers);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("embedding")) {
                Map<String, Object> embeddingNode = (Map<String, Object>) response.get("embedding");
                List<Double> values = (List<Double>) embeddingNode.get("values");
                cloudDims = values.size(); // usually 768
            }
        } catch (Exception e) {
            System.err.println("Cloud Embedding failed: " + e.getMessage());
        }
        long cloudEnd = System.currentTimeMillis();

        result.put("cloudModel", Map.of(
            "name", "text-embedding-004",
            "provider", "Google Gemini (Cloud API)",
            "dimensions", cloudDims > 0 ? cloudDims : "Error",
            "timeMs", cloudEnd - cloudStart
        ));

        return result;
    }
}
