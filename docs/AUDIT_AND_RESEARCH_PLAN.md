# Rà soát yêu cầu và kế hoạch nghiên cứu

Tài liệu này phản ánh source code hiện tại của repository tại thời điểm tháng
7/2026. Không xem các tính năng chỉ xuất hiện trong ảnh hoặc bản sao khác là đã
được triển khai nếu chúng không tồn tại trong checkout này.

## 1. Kết luận hiện trạng

Backend đã có các luồng chính: quản lý tài liệu, lưu metadata, chunk bằng
`TokenTextSplitter`, index vào PGVector, chat RAG, session/message trong
PostgreSQL, gọi local fine-tuned API và ba endpoint evaluation.

Tuy nhiên source hiện tại vẫn là prototype nghiên cứu:

- Frontend checkout hiện thiếu `package.json` và `src`, nên chưa thể build lại
  web app trực tiếp từ repository này.
- RAG trả source bằng nội dung prompt, chưa có citation DTO gồm trang/slide/chunk.
- Lịch sử được lưu trong DB nhưng chưa được đưa lại vào prompt làm chat memory.
- Upload dùng một chunker cố định; ba class chunking strategy chưa được nối vào
  `DocumentService`.
- Endpoint benchmark mới đo câu trả lời/latency, số chunks và dimensions; chưa
  chạy batch 50 câu hoặc tự tính RAGAS/retrieval metrics.

## 2. Ma trận đối chiếu yêu cầu

| Yêu cầu | Trạng thái trong source hiện tại | Việc cần hoàn thiện/xác minh |
|---|---|---|
| Upload PDF, DOCX, slide | Có qua `TikaDocumentReader` | Test file thật; bổ sung OCR nếu PDF scan |
| Tự động chunk và embed | Có | Upload đang dùng `TokenTextSplitter` duy nhất |
| Quản lý môn/chương | Có metadata | Chuẩn hóa danh mục môn/chương |
| Danh sách tài liệu index | Có `GET /api/documents` | Hiển thị trạng thái rõ trên frontend |
| Xóa tài liệu và vector | Có | `DocumentService` xóa theo `metadata.doc_id` |
| Chat theo phiên | Có và lưu DB | Đưa lịch sử cũ vào prompt để giữ ngữ cảnh |
| Citation | Một phần | Hiện chèn filename trong prompt; cần DTO cấu trúc |
| Giới hạn trong tài liệu | Prompt-based | Thêm retrieval threshold và test refusal |
| Fine-tuned model thật | Có FastAPI + Qwen/LoRA | Xác minh checkpoint và môi trường thư viện |
| RAG vs fine-tuning | Có `/api/evaluation/compare` | Hiện trả answer, sources, latency; chưa chấm accuracy/F1 |
| Benchmark chunking | Có endpoint cơ bản | Hiện so Token/Sentence/Paragraph theo time/chunk count |
| Fixed/Semantic/Hierarchical | Có class | Chưa được endpoint/upload gọi trực tiếp |
| Benchmark embedding | Có endpoint cơ bản | So all-MiniLM-L6-v2 và Gemini text-embedding-004 |
| E5/PhoBERT/BGE-M3/OpenAI | Chưa nối vào endpoint Java | Cần adapter/runner và index riêng từng dimension |
| Dashboard benchmark | Chưa xác minh trong checkout | Source frontend hiện không đầy đủ |
| Test set 50 câu + ground truth | Chưa có CSV hoàn chỉnh trong checkout | Cần import/khôi phục artifact chính thức |
| Bảng RAGAS | Chưa có artifact đọc được trong checkout | Cần source script và raw/summary results |

## 3. Những điểm cần sửa ưu tiên

### P0 — khả năng chạy và tính nhất quán

1. Khôi phục đầy đủ source frontend (`package.json`, `src`, assets cần thiết).
2. Không hard-code Gemini key; dùng `SPRING_AI_OPENAI_API_KEY`.
3. Đổi `spring.jpa.hibernate.ddl-auto=create` sang `update` hoặc Flyway để không
   tạo lại schema/làm mất dữ liệu mỗi lần khởi động.
4. Thêm validation và exception response rõ cho upload/chat/evaluation.
5. Thêm unit/integration test cho upload, delete vector, RAG refusal và session.

### P1 — hoàn thiện RAG

1. Chuẩn hóa `ChunkingStrategy` và cho `DocumentService` chọn fixed-size,
   semantic hoặc hierarchical từ request.
2. Lưu `chunkId`, `page/slide`, `filename`, `subject`, `chapter`, chunk strategy
   và embedding model trong metadata.
3. Trả citation có cấu trúc thay vì chỉ chèn tên file vào answer.
4. Đưa các message trước của session vào prompt với giới hạn cửa sổ hội thoại.
5. Thêm similarity threshold và câu từ chối cố định khi retrieval không đủ tốt.

### P2 — thực nghiệm có thể tái lập

1. Chốt một corpus môn học và tạo manifest gồm file, chương, checksum/version.
2. Chuẩn bị 50 câu hỏi, ground truth và evidence page/slide do con người duyệt.
3. Tạo index riêng cho từng `(corpus, chunker, embedding model)`.
4. Đánh giá retrieval bằng Hit@k/Recall@k, MRR và nDCG@k.
5. Đánh giá generation bằng faithfulness, answer relevancy, context precision,
   context recall và human review/LLM judge có rubric.
6. Chạy cùng test set cho base model, fine-tuned model và RAG; lưu latency p50,
   p95, chi phí API/GPU, thời gian index/retrain.
7. Lưu raw per-question results, cấu hình và Git commit cho từng run.

## 4. Thiết kế benchmark phù hợp với câu hỏi nghiên cứu

### RQ chính: RAG so với fine-tuning

Giữ cố định test set và rubric. So sánh:

- Độ chính xác/faithfulness.
- Tỷ lệ từ chối đúng với câu ngoài phạm vi.
- Latency và chi phí triển khai.
- Thời gian/công sức cập nhật khi tài liệu thay đổi.

### RQ phụ 1: Chunking strategy

Giữ cố định embedding model và top-k; thay đổi fixed-size, semantic,
hierarchical. Báo Recall@k, MRR, nDCG@k và latency. Không kết luận strategy tốt
nhất chỉ dựa trên số chunks hoặc thời gian chia văn bản.

### RQ phụ 2: Embedding model

Giữ cố định corpus, chunking và top-k; tạo index riêng cho multilingual-e5,
PhoBERT, BGE-M3 và OpenAI nếu có credit. PhoBERT cần mô tả rõ pooling hoặc
fine-tuning vì không phải sentence embedding model mặc định.

## 5. Cấu trúc deliverables đề xuất

```text
docs/
  Nhóm Lập Trình Java.md
  sequence-diagrams.drawio
  RUN_WINDOWS.md
  AUDIT_AND_RESEARCH_PLAN.md
evaluation/
  test-set.csv
  experiment-matrix.csv
  runs/<run-id>/raw-results.csv
  runs/<run-id>/summary.json
reports/
  experimental-report.md
  ragas-benchmark.csv
  figures/
data/
  corpus-manifest.csv
```

Mỗi run nên lưu timestamp, Git commit, corpus version, chunker config,
embedding model/revision, generator/checkpoint, prompt hash, top-k, temperature
và seed. Không dùng số liệu giả làm kết quả thực nghiệm chính thức.

## 6. Liên kết tài liệu

- [Báo cáo nhóm](./Nhóm%20Lập%20Trình%20Java.md)
- [Sơ đồ sequence](./sequence-diagrams.drawio)
- [Hướng dẫn chạy Windows](./RUN_WINDOWS.md)
- [README dự án](../README.md)

