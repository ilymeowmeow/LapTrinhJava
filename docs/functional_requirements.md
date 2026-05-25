# Yêu Cầu Chức Năng (Functional Requirements)
Dự án: Chatbot hỏi đáp tài liệu môn học (So sánh RAG vs Fine-tuning)

## 1. Chức năng dành cho Sinh viên (Người dùng)
* **Đăng nhập / Xác thực:** Sinh viên đăng nhập để lưu trữ và quản lý lịch sử trò chuyện.
* **Giao diện Hỏi đáp (Chat interface):**
    * Gửi câu hỏi bằng tiếng Việt liên quan đến tài liệu môn học.
    * Nhận phản hồi từ Chatbot dưới dạng text-streaming (hiển thị chữ đến đâu chạy đến đó).
    * Xem nguồn trích dẫn tài liệu (đối với mô hình sử dụng cấu trúc RAG - ghi rõ trang, tên tài liệu).
* **Quản lý hội thoại:** Tạo đoạn chat mới, xóa hội thoại cũ, xem lại lịch sử.
* **Phản hồi chất lượng (Feedback):** Đánh giá câu trả lời (Like/Dislike) để thu thập dữ liệu kiểm thử.

## 2. Chức năng dành cho Quản trị viên / Giảng viên
* **Quản lý kho tri thức (Knowledge Base):**
    * Tải lên tài liệu môn học mới (định dạng PDF, DOCX, TXT).
    * Hệ thống tự động phân tách dữ liệu (Chunking) và lưu vào Vector Database.
    * Xóa hoặc chỉnh sửa các tài liệu cũ/lỗi thời.

## 3. Chức năng Nghiên cứu & Đánh giá (Admin/Tester)
* **Chuyển đổi cấu hình mô hình (Model Switcher):** Cho phép chọn chạy chatbot bằng cơ chế **RAG** hoặc mô hình đã **Fine-tuned**.
* **Hệ thống giám sát hiệu năng (Evaluation Logging):** Ghi nhận thời gian phản hồi (latency), điểm số tương đồng hoặc các chỉ số đánh giá (BLEU/ROUGE) của 2 phương pháp để làm số liệu báo cáo.