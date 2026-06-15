# Chatbot AI Education 🤖📚

Hệ thống Trợ lý Hỏi đáp thông minh dành cho giáo dục, hỗ trợ sinh viên với khả năng truy xuất tài liệu (RAG Mode) và sử dụng kiến thức nội tại của LLM (Fine-Tuning Mode).

Dự án này bao gồm hai phần:
- **Backend:** Java Spring Boot, Spring AI, PostgreSQL (PGVector).
- **Frontend:** Next.js (React), Tailwind CSS.

---

## 🚀 Hướng dẫn Cài đặt & Khởi chạy

### 1. Yêu cầu hệ thống (Prerequisites)
Để chạy được dự án, máy tính của bạn (và người bạn chia sẻ) cần cài đặt sẵn:
- **Java 17** (Dùng cho Spring Boot Backend)
- **Node.js (v18 hoặc v20+)** (Dùng cho Next.js Frontend)
- **Docker Desktop** (Dùng để chạy Database PostgreSQL với PGVector)
- **Git** (Để clone source code)
- **Maven** (Để build và chạy Java)

### 2. Thiết lập Database (Postgres + PGVector)
Hệ thống lưu trữ dữ liệu và vector của tài liệu vào PostgreSQL.
1. Mở Terminal / CMD tại thư mục gốc của dự án.
2. Chạy lệnh sau để khởi động Database:
   ```bash
   docker-compose up -d
   ```
   *(Đợi Docker kéo image về và khởi chạy container `rag_db` trên port `5432`)*.
3. Database `rag_db` và Extension `vector` sẽ tự động được tạo và cấu hình.

### 3. Thiết lập API Key (Bảo mật)
Dự án sử dụng Gemini (Google) và Llama 3 (Groq). Bạn KHÔNG ĐƯỢC hard-code trực tiếp key vào code để tránh bị mất cắp.
1. Tạo các biến môi trường (Environment Variables) trên hệ điều hành của bạn, hoặc thêm vào cấu hình chạy của IDE (IntelliJ, Eclipse, VSCode):
   - `OPENAI_API_KEY`: Điền API Key của Google Gemini vào đây.
   - `GROQ_API_KEY`: Điền API Key của Groq vào đây.
2. Spring Boot sẽ tự động đọc các biến môi trường này vào `application.yml` khi ứng dụng khởi chạy.

### 4. Khởi chạy Backend (Spring Boot)
1. Mở một Terminal / CMD và di chuyển vào thư mục `backend/`:
   ```bash
   cd backend
   ```
2. Chạy ứng dụng bằng Maven:
   ```bash
   mvn clean spring-boot:run
   ```
3. Đợi vài giây cho đến khi terminal hiện dòng chữ `Started ChatbotApplication...`. Backend sẽ khởi chạy và lắng nghe tại: **http://localhost:8080**

### 5. Khởi chạy Frontend (Next.js)
1. Mở một Terminal / CMD MỚI, giữ nguyên terminal của Backend đang chạy.
2. Di chuyển vào thư mục `frontend/`:
   ```bash
   cd frontend
   ```
3. Cài đặt các thư viện cần thiết (Chỉ cần chạy lần đầu):
   ```bash
   npm install
   ```
4. Khởi chạy ứng dụng Frontend:
   ```bash
   npm run dev
   ```
5. Mở trình duyệt và truy cập: **http://localhost:3000** để trải nghiệm ứng dụng.

---

## 🛠 Cấu trúc dự án
- `/backend`: Mã nguồn Spring Boot, xử lý API, RAG, nhúng Vector và kết nối LLM.
  - Tích hợp Native Gemini API (sử dụng Model: `gemini-robotics-er-1.6-preview` & `gemini-embedding-2`).
  - Tích hợp Groq API (sử dụng Model: `llama-3.1-8b-instant`).
- `/frontend`: Mã nguồn giao diện Next.js, hiển thị Chat UI, Quản lý tài liệu và Đánh giá Model.
- `/scripts`: Các script Python hỗ trợ huấn luyện dữ liệu nội bộ (Local Fine-Tuning).
- `.cursorrules`: Cấu hình nguyên tắc lập trình cho AI Agent (như Cursor/Copilot) để không làm vỡ kiến trúc code.

---

## 📝 Lưu ý quan trọng cho Developers & AI Agents
Dự án này đã thiết lập file `.cursorrules` ở thư mục gốc. **Tuyệt đối không được xoá** và khi nếu dùng AI để coding, AI đó sẽ tự động tuân thủ các quy tắc bảo vệ cấu trúc này (VD: Không phá lớp tích hợp Native Gemini, không hard-code API Keys).
