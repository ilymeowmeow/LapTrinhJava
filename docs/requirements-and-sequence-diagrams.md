# Đối chiếu yêu cầu và sequence diagram

## Tài liệu sơ đồ

- File Draw.io chính thức: [`sequence-diagrams.drawio`](./sequence-diagrams.drawio).
- File gồm 8 trang, được trình bày theo mẫu UML sequence ShopLite: actor, participant, lifeline, activation bar, message đánh số, return nét đứt và UML frame.
- Các trang trong file:
  1. Google OAuth.
  2. Upload và index tài liệu.
  3. RAG Chat.
  4. Chat Session.
  5. Fine-tuned Chat.
  6. A/B Benchmark.
  7. Chunking Benchmark.
  8. Embedding Benchmark.
- Ba trang benchmark được gắn nhãn **Dự kiến / chưa xác minh end-to-end** nếu chức năng chưa có đầy đủ trong source Git.
- Các khối Mermaid phía dưới là bản mô tả bằng văn bản để review trên GitHub; khi xuất PNG/PDF hoặc chỉnh sửa trực quan, sử dụng file `.drawio` ở trên.

## 1. Phạm vi rà soát

- Source được rà soát: nhánh `main`, commit local `2fd06ff`.
- Remote `origin/main` đang ở `b17bc5e`, đi trước 2 commit nhưng chỉ thay đổi `README.md`, không thêm code benchmark.
- Ảnh giao diện Research Module do thành viên cung cấp được ghi nhận như bằng chứng demo. Những phần không có trong source Git được đánh dấu **demo/chưa xác minh bằng source**.

## 2. Đối chiếu yêu cầu ban đầu

| Nhóm | Yêu cầu | Trạng thái trong source Git | Bằng chứng / khoảng trống |
|---|---|---|---|
| Quản lý tài liệu | Upload PDF, DOCX, slide | Đạt một phần | UI nhận `.pdf,.docx,.pptx`; backend dùng Tika. Chưa kiểm tra MIME/extension và chưa có test parser. |
| Quản lý tài liệu | Tự động chunk và embed | Đạt một phần | `DocumentService` dùng `TokenTextSplitter` rồi `vectorStore.add`. Tuy nhiên `spring.ai.openai.embedding.enabled=false`, cần xác minh `EmbeddingModel` lúc chạy. |
| Quản lý tài liệu | Theo môn/chương | Đạt | `subject`, `chapter` được lưu vào metadata và DB. |
| Quản lý tài liệu | Danh sách tài liệu đã index | Đạt | `GET /api/documents`, trạng thái `PROCESSING/INDEXED/FAILED`. |
| Quản lý tài liệu | Xóa tài liệu | Đạt một phần | Backend có `DELETE /api/documents/{id}` và xóa vector; trang `/documents` chưa có thao tác DELETE. |
| Chat | Hỏi đáp tự nhiên | Đạt cơ bản | Có RAG chat qua Gemini và chế độ local fine-tuned. |
| Chat | Theo ngữ cảnh hội thoại | Chưa đạt | Tin nhắn được lưu nhưng lịch sử không được đưa vào prompt; trang `/chat` dùng session mặc định ID 1. |
| Chat | Trích dẫn nguồn | Đạt một phần | Prompt yêu cầu LLM tự ghi tên file. `ChatResponse`/`SourceDto` tồn tại nhưng controller chỉ trả `{answer}`, nên UI không nhận structured citations. |
| Chat | Giới hạn trong tài liệu | Đạt một phần | Có prompt từ chối ngoài phạm vi; chưa có kiểm tra retrieval rỗng/threshold độc lập và chưa có test refusal. |
| Chat | Lịch sử theo phiên | Đạt một phần | Có API tạo/xem/xóa session và lưu message. `ddl-auto=create` làm mất dữ liệu khi backend restart; frontend `/chat` chưa dùng đầy đủ API session. |
| RBL | RAG vs fine-tuned | Đạt một phần | Hai nhánh xử lý tồn tại. UI `/research` gọi `/api/evaluation/compare`, nhưng backend không có endpoint này. |
| RBL | Benchmark chunking | Chưa tích hợp | Có 3 class fixed/semantic/hierarchical nhưng không được `DocumentService` sử dụng; thiếu file `ChunkingStrategy.java` trong Git. |
| RBL | Benchmark embedding | Chưa có | Không có runner, endpoint, dataset hay bảng kết quả trong source Git hiện tại. |
| RBL | Dashboard thực nghiệm | Demo/đạt một phần | A/B UI và biểu đồ latency đã commit. Giao diện 3 tab trong ảnh chưa có trên Git và endpoint backend còn thiếu. |
| Deliverable | Web app Java + React | Có | Spring Boot + Next.js. Chưa xác minh build end-to-end vì Maven PKIX và Google Fonts network. |
| Deliverable | README trên GitHub | Có | Remote vừa cập nhật README; một số mô tả vượt quá code thực tế. |
| Deliverable | Test set 50 câu + ground truth | Chưa có trên Git | Không có thư mục/file evaluation được track. |
| Deliverable | Báo cáo thực nghiệm | Chưa có trên Git | Không có report hoặc bảng RAGAS được track. |
| Deliverable | Fine-tuned model | Có artifact | Adapter/checkpoint được commit; cần kiểm tra lại dung lượng repo và khả năng serve. |

## 3. Sequence diagram theo source thực tế

### 3.1. Đăng nhập Google OAuth

```mermaid
sequenceDiagram
    actor SV as Sinh viên
    participant FE as Next.js / NextAuth
    participant GG as Google OAuth

    SV->>FE: Chọn đăng nhập Google
    FE->>GG: Authorization request (client_id, redirect_uri)
    GG-->>SV: Màn hình đăng nhập/đồng ý
    SV->>GG: Xác thực tài khoản
    GG-->>FE: Callback + authorization code
    FE->>GG: Đổi code lấy token/profile
    GG-->>FE: Token + thông tin người dùng
    FE-->>SV: Tạo session và hiển thị ứng dụng

    Note over FE: Cần GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET,<br/>NEXTAUTH_URL và NEXTAUTH_SECRET trong .env.local
```

### 3.2. Upload, chunk và index tài liệu

```mermaid
sequenceDiagram
    actor GV as Giảng viên
    participant FE as Documents UI
    participant DC as DocumentController
    participant DS as DocumentService
    participant DB as PostgreSQL
    participant Tika as TikaDocumentReader
    participant Split as TokenTextSplitter
    participant VS as PGVector VectorStore
    participant EM as Embedding model

    GV->>FE: Chọn file + môn học + chương
    FE->>DC: POST /api/documents/upload (multipart)
    DC->>DS: uploadAndIndex(file, subject, chapter)
    DS->>DB: Lưu metadata, status=PROCESSING
    DB-->>DS: documentId
    DS->>Tika: Trích xuất nội dung PDF/DOCX/PPTX
    Tika-->>DS: Danh sách Document
    DS->>DS: Gắn doc_id, subject, chapter, filename
    DS->>Split: Chia văn bản thành chunks
    Split-->>DS: Chunked documents
    DS->>VS: add(chunks)
    VS->>EM: Sinh embedding cho chunks
    EM-->>VS: Vector 768 chiều
    VS->>DB: Lưu vector + metadata
    DS->>DB: Cập nhật status=INDEXED
    DS-->>DC: CourseDocument
    DC-->>FE: 200 + metadata
    FE-->>GV: Hiển thị tài liệu đã index

    alt Có lỗi parse/embed/index
        DS->>DB: Cập nhật status=FAILED
        DC-->>FE: 500 Internal Server Error
    end
```

### 3.3. Xem và xóa tài liệu

```mermaid
sequenceDiagram
    actor GV as Giảng viên
    participant FE as Documents UI
    participant DC as DocumentController
    participant DS as DocumentService
    participant DB as PostgreSQL / PGVector

    GV->>FE: Mở trang Documents
    FE->>DC: GET /api/documents
    DC->>DS: getAllDocuments()
    DS->>DB: SELECT course_documents
    DB-->>DS: Danh sách tài liệu
    DS-->>FE: JSON danh sách tài liệu

    opt Xóa tài liệu (backend đã có, UI chưa nối)
        GV->>FE: Chọn xóa tài liệu
        FE->>DC: DELETE /api/documents/{id}
        DC->>DS: deleteDocument(id)
        DS->>DB: DELETE vector_store WHERE metadata.doc_id=id
        DS->>DB: DELETE course_document WHERE id=id
        DC-->>FE: 200 OK
    end
```

### 3.4. Hỏi đáp bằng RAG

```mermaid
sequenceDiagram
    actor SV as Sinh viên
    participant FE as Chat UI
    participant CC as ChatController
    participant CS as ChatService
    participant DB as PostgreSQL
    participant VS as PGVector
    participant GM as Gemini ChatModel

    SV->>FE: Nhập câu hỏi, chọn RAG
    FE->>CC: POST /api/chat {query, subject, mode=rag}
    CC->>DB: Tìm/tạo session mặc định ID 1
    CC->>CS: askQuestion(sessionId, question, RAG, subject)
    CS->>DB: Lưu USER message
    CS->>VS: similaritySearch(question, topK=5, filter subject)
    VS-->>CS: Các chunks gần nhất + metadata
    CS->>CS: Ghép context và prompt giới hạn phạm vi
    CS->>GM: Prompt(context, question)
    GM-->>CS: Câu trả lời có trích dẫn dạng text
    CS->>DB: Lưu BOT message
    CS-->>CC: answer
    CC-->>FE: {answer}
    FE-->>SV: Hiển thị câu trả lời

    Note over CS,DB: Lịch sử đã lưu nhưng chưa được nạp vào prompt.
    Note over CC,FE: Controller chưa trả sources có cấu trúc dù DTO đã tồn tại.
```

### 3.5. Quản lý phiên và lịch sử chat

```mermaid
sequenceDiagram
    actor SV as Sinh viên
    participant FE as Chat UI
    participant CC as ChatController
    participant SR as ChatSessionRepository
    participant MR as ChatMessageRepository

    SV->>FE: Tạo cuộc trò chuyện mới
    FE->>CC: POST /api/chat/session {title}
    CC->>SR: save(ChatSession)
    SR-->>CC: Session có ID
    CC-->>FE: Session JSON

    SV->>FE: Mở lại một phiên
    FE->>CC: GET /api/chat/session/{id}/messages
    CC->>MR: findBySessionIdOrderByCreatedAtAsc(id)
    MR-->>CC: Danh sách USER/BOT messages
    CC-->>FE: Lịch sử theo thời gian

    opt Xóa phiên
        FE->>CC: DELETE /api/chat/session/{id}
        CC->>SR: deleteById(id)
        SR-->>CC: Đã xóa session và messages cascade
        CC-->>FE: 200 OK
    end
```

### 3.6. Hỏi đáp bằng fine-tuned model local

```mermaid
sequenceDiagram
    actor SV as Sinh viên
    participant FE as Chat/Fine-tuning UI
    participant CC as ChatController
    participant CS as ChatService
    participant LM as Local model endpoint
    participant DB as PostgreSQL

    SV->>FE: Chọn Fine-tune và nhập câu hỏi
    FE->>CC: POST /api/chat với mode=finetune, localModelEndpoint
    CC->>CS: askQuestion(..., Fine-tuning Mode, endpoint)
    CS->>DB: Lưu USER message

    alt Có local endpoint
        CS->>LM: POST {model: llama3, prompt, stream:false}
        LM-->>CS: {response}
        CS->>DB: Lưu BOT message
        CS-->>FE: Câu trả lời local model
    else Chưa cấu hình endpoint
        CS->>DB: Lưu thông báo lỗi cấu hình
        CS-->>FE: Yêu cầu cấu hình endpoint
    end
```

## 4. Sequence diagram chức năng benchmark theo ảnh

> Trạng thái: **UI A/B cơ bản đã commit; backend evaluation và giao diện 3 tab chưa có trong source Git**. Diagram dưới đây ghi nhận luồng mong muốn để thiết kế/tài liệu, không khẳng định đã triển khai end-to-end.

### 4.1. A/B Benchmark: RAG vs fine-tuned model

```mermaid
sequenceDiagram
    actor NC as Người nghiên cứu
    participant UI as Research Dashboard
    participant EC as EvaluationController
    participant RAG as RAG pipeline
    participant VS as PGVector
    participant GM as Gemini
    participant FT as Fine-tuned local endpoint
    participant MET as Metrics aggregator

    NC->>UI: Nhập câu hỏi + local endpoint
    UI->>EC: POST /api/evaluation/compare

    par Chạy RAG
        EC->>RAG: evaluate(question)
        RAG->>VS: Retrieve top-k chunks
        VS-->>RAG: Context + sources
        RAG->>GM: Generate answer với context
        GM-->>RAG: RAG answer
        RAG-->>EC: answer, sources, latency
    and Chạy fine-tuned
        EC->>FT: POST /api/generate {question}
        FT-->>EC: Fine-tuned answer + latency
    end

    EC->>MET: So sánh accuracy/F1, hallucination, latency
    MET-->>EC: Evaluation result
    EC-->>UI: Hai answer + sources + metrics
    UI-->>NC: Cards kết quả và biểu đồ latency
```

### 4.2. Benchmark chunking strategies

```mermaid
sequenceDiagram
    actor NC as Người nghiên cứu
    participant UI as Chunking tab
    participant BEN as Benchmark service
    participant FIX as Fixed-size chunker
    participant SEM as Semantic chunker
    participant HIE as Hierarchical chunker
    participant EM as Embedding model cố định
    participant RET as Retrieval evaluator

    NC->>UI: Chọn corpus + test set + top-k
    UI->>BEN: Chạy benchmark 3 strategies
    loop Mỗi strategy
        BEN->>FIX: Chunk nếu fixed-size
        BEN->>SEM: Chunk nếu semantic
        BEN->>HIE: Chunk nếu hierarchical
        BEN->>EM: Embed chunks và questions
        BEN->>RET: Tính Hit@k, MRR, nDCG@k
        RET-->>BEN: Kết quả strategy
    end
    BEN-->>UI: Bảng so sánh + strategy tốt nhất
    UI-->>NC: Hiển thị chart/table
```

### 4.3. Benchmark embedding models

```mermaid
sequenceDiagram
    actor NC as Người nghiên cứu
    participant UI as Embedding tab
    participant BEN as Benchmark service
    participant E5 as multilingual-e5
    participant PHO as PhoBERT
    participant BGE as BGE-M3
    participant OAI as OpenAI embedding
    participant RET as Retrieval evaluator

    NC->>UI: Chọn chunking cố định + test set
    UI->>BEN: Chạy embedding benchmark
    loop Mỗi embedding model khả dụng
        BEN->>E5: Embed corpus/questions nếu E5
        BEN->>PHO: Embed corpus/questions nếu PhoBERT
        BEN->>BGE: Embed corpus/questions nếu BGE-M3
        BEN->>OAI: Embed corpus/questions nếu có API credit
        BEN->>RET: Tính Hit@k, MRR, nDCG@k
        RET-->>BEN: Kết quả model
    end
    BEN-->>UI: Bảng model + chi phí + latency
    UI-->>NC: Hiển thị model phù hợp nhất
```

## 5. Ưu tiên cần hoàn thiện nếu tiếp tục code

1. Thêm lại `ChunkingStrategy.java` để backend có thể compile.
2. Bật/cấu hình embedding model tương thích với PGVector 768 chiều.
3. Trả `ChatResponse(answer, sources)` thay vì chỉ `{answer}`.
4. Đưa lịch sử message vào prompt và bỏ session mặc định cố định cho `/api/chat`.
5. Tạo `EvaluationController`/service cho `/api/evaluation/compare` hoặc đổi UI theo endpoint thật.
6. Kết nối 3 chunking strategy vào pipeline benchmark; bổ sung embedding runner.
7. Commit test set 50 câu, raw/summary benchmark và báo cáo thực nghiệm.
8. Thêm unit/integration tests và đổi `ddl-auto=create` sang cấu hình không xóa lịch sử khi restart.
