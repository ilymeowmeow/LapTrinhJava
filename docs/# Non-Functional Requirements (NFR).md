# Non-Functional Requirements (NFR)
## Hệ thống Chatbot hỏi đáp tài liệu môn học (So sánh RAG vs Fine-tuning)

---

## 1. Performance (Hiệu năng)

- Thời gian phản hồi chatbot < 5 giây cho mỗi câu hỏi
- Hỗ trợ tối thiểu 30–40 người dùng đồng thời
- Hỗ trợ streaming response (hiển thị câu trả lời theo thời gian thực)
- Truy vấn RAG được tối ưu để giảm độ trễ
- Thời gian xử lý tài liệu (upload + indexing) < 10 giây với file < 10MB

---

## 2. Scalability (Khả năng mở rộng)

- Có thể mở rộng thêm nhiều môn học và tài liệu
- Dễ dàng tích hợp thêm mô hình AI (RAG, Fine-tuning, embedding khác)
- Backend thiết kế module hóa (Spring Boot)

---

## 3. Maintainability (Khả năng bảo trì)

- Tuân thủ nguyên lý SOLID
- Áp dụng kiến trúc phân tầng:
  - Controller
  - Service
  - Repository
- Dễ dàng thay đổi:
  - Embedding model
  - Chat model
  - Vector Database
- Code có cấu trúc rõ ràng, dễ hiểu

---

## 4. Usability (Tính dễ sử dụng)

### Sinh viên (User)
- Giao diện đơn giản, dễ sử dụng
- Chỉ thực hiện:
  - Hỏi đáp chatbot
  - Xem lịch sử hội thoại

### Giảng viên / Admin
- Upload và quản lý tài liệu
- Theo dõi trạng thái xử lý dữ liệu

---

## 5. Security (Bảo mật & Phân quyền)

### Phân quyền hệ thống (RBAC)

#### Student
- Được phép:
  - Hỏi chatbot
  - Xem lịch sử chat
- Không được phép:
  - Upload tài liệu
  - Chỉnh sửa / xóa tài liệu
  - Thay đổi dữ liệu hệ thống

#### Admin / Lecturer
- Được phép:
  - Upload tài liệu (PDF, DOCX, TXT)
  - Xóa / chỉnh sửa tài liệu
  - Quản lý kho tri thức (Knowledge Base)
  - Theo dõi trạng thái indexing (RAG)

---

### Các yêu cầu bảo mật khác

- Xác thực người dùng (login)
- Validate input để tránh injection
- Giới hạn file upload:
  - < 10MB
  - Định dạng hợp lệ
- Người dùng chỉ truy vấn trên dữ liệu có sẵn, không thể thay đổi nguồn dữ liệu

---

## 6. Reliability (Độ tin cậy)

- Hệ thống không bị crash khi input sai
- Nếu không tìm được câu trả lời → trả về thông báo phù hợp
- Hoạt động ổn định trong quá trình demo
- Lưu phản hồi người dùng (Like/Dislike) để phục vụ đánh giá hệ thống

---

## 7. Compatibility (Tương thích)

- Trình duyệt:
  - Chrome
  - Edge
- Backend:
  - Java 17+
- Frontend:
  - React / NextJS

---

## 8. Deployment (Triển khai)

- Backend: Spring Boot (Java)
- Frontend: React / NextJS
- Giao tiếp qua REST API
- Có thể deploy:
  - Local
  - Render / Railway / Vercel

---

## 9. Logging & Monitoring

- Ghi log các hoạt động:
  - Chat
  - Upload tài liệu (admin)
  - Feedback người dùng
- Log lỗi để debug
- Không yêu cầu hệ thống monitoring phức tạp (phù hợp scope sinh viên)

---

## 10. Constraints (Ràng buộc)

- Backend bắt buộc: Java Spring Boot
- Frontend: React hoặc NextJS

---

## 11. AI Constraints

- Hỗ trợ 2 phương pháp:
  - RAG (Retrieval-Augmented Generation)
  - Fine-tuning
- Cho phép chuyển đổi giữa các mô hình để phục vụ so sánh (Model Switcher)
- Dữ liệu đầu vào chỉ đến từ tài liệu do Admin cung cấp
- Sử dụng embedding model hỗ trợ tiếng Việt
- Áp dụng:
  - Chunking
  - Embedding
  - Vector Database

---

## 12. Evaluation (Đánh giá hệ thống)

- Sử dụng bộ test khoảng 50 câu hỏi
- Ghi nhận các chỉ số:
  - Thời gian phản hồi (Latency)
  - BLEU / ROUGE
  - Độ chính xác (Answer correctness)
  - Độ liên quan ngữ cảnh (Context relevance)
- Có thể đánh giá bằng:
  - Manual (con người)
  - RAGAS (nâng cao) 

---
