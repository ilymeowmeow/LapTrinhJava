# Chatbot AI Education 🤖📚

Hệ thống Trợ lý Hỏi đáp thông minh dành cho giáo dục, hỗ trợ sinh viên với khả năng truy xuất tài liệu (RAG Mode) và sử dụng kiến thức nội tại của LLM thông qua huấn luyện (Fine-Tuning Mode).

Dự án này bao gồm 3 phần chính:
- **Backend:** Java Spring Boot, Spring AI, PostgreSQL (PGVector).
- **Frontend:** Next.js (React), Tailwind CSS, NextAuth (Đăng nhập Google).
- **Finetuning:** Python, Unsloth, Hugging Face (Huấn luyện mô hình ngôn ngữ lớn).

---

## 🚀 Hướng dẫn Cài đặt & Khởi chạy toàn bộ dự án

### 1. Yêu cầu hệ thống (Prerequisites)
Để chạy được toàn bộ hệ thống, máy tính của bạn cần cài đặt:
- **Java 17** (Dùng cho Spring Boot Backend)
- **Node.js (v18 hoặc v20+)** (Dùng cho Next.js Frontend)
- **Docker Desktop** (Dùng để chạy Database PostgreSQL với PGVector)
- **Git** (Để clone source code)
- **Maven** (Để build và chạy Java)
- **Python 3.10+** (Dùng cho quá trình Fine-Tuning AI)

### 2. Thiết lập Database (Postgres + PGVector)
Hệ thống lưu trữ dữ liệu và vector của tài liệu vào PostgreSQL.
1. Mở Terminal / CMD tại thư mục gốc của dự án.
2. Chạy lệnh sau để khởi động Database bằng Docker:
   ```bash
   docker-compose up -d
   ```
   *(Đợi Docker kéo image về và khởi chạy container `rag_db` trên port `5432`)*.
3. Database `rag_db` và Extension `vector` sẽ tự động được tạo và sẵn sàng sử dụng.

---

### 3. Hướng dẫn Lấy và Thiết lập API Key

#### A. Thiết lập API Key Gemini (Cho Backend)
Hệ thống Backend sử dụng Gemini làm bộ não (LLM) để xử lý Chat và sinh câu trả lời trong quá trình RAG.
1. Truy cập [Google AI Studio](https://aistudio.google.com/) và đăng nhập bằng tài khoản Google.
2. Nhấn vào **Get API key** ở thanh menu bên trái và tạo một API Key mới.
3. Trong hệ điều hành của bạn, tạo biến môi trường tên là `OPENAI_API_KEY` và dán giá trị API Key vừa lấy vào.
   - *(Lưu ý: Hệ thống sử dụng thư viện kết nối OpenAI nhưng gọi tới endpoint của Google Gemini, nên biến môi trường được đặt là `OPENAI_API_KEY`)*.
   - Hoặc, bạn có thể mở trực tiếp file `backend/src/main/resources/application.yml` và thay chuỗi `YOUR_API_KEY_HERE` ở dòng `api-key:` bằng key thực tế của bạn *(không khuyến khích nếu bạn có ý định push code)*.

#### B. Thiết lập API Key Đăng nhập Google (Cho Frontend)
Frontend sử dụng thư viện NextAuth để cho phép sinh viên đăng nhập bằng Google OAuth2.
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/).
2. Bấm vào Dropdown chọn dự án ở thanh Top-bar -> Chọn **New Project** và tạo một dự án mới.
3. Mở Menu bên trái, đi tới **APIs & Services** > **Credentials**.
4. (Nếu chưa từng làm) Chọn **OAuth consent screen**, điền tên ứng dụng và email hỗ trợ, sau đó lưu lại.
5. Nhấn **Create Credentials** > Chọn **OAuth client ID**. 
6. Đặt Application type là **Web application**.
7. Tại phần **Authorized redirect URIs**, thêm địa chỉ sau: 
   `http://localhost:3000/api/auth/callback/google`
8. Nhấn **Create**. Bạn sẽ được cung cấp `Client ID` và `Client secret`.
9. Vào thư mục `frontend/`, mở (hoặc tạo mới) file tên là `.env.local` và điền nội dung cấu hình như sau:
   ```env
   GOOGLE_CLIENT_ID=Dán_Client_ID_của_bạn_vào_đây
   GOOGLE_CLIENT_SECRET=Dán_Client_Secret_của_bạn_vào_đây
   NEXTAUTH_URL=http://localhost:3000
   NEXTAUTH_SECRET=chuoi_ky_tu_bi_mat_bat_ky_cua_ban
   ```

---

### 4. Hướng dẫn Khởi chạy Hệ thống

#### Bước 4.1. Khởi chạy Backend (Spring Boot)
1. Mở Terminal / CMD mới và di chuyển vào thư mục `backend/`:
   ```bash
   cd backend
   ```
2. Chạy ứng dụng bằng lệnh Maven:
   ```bash
   mvn clean spring-boot:run
   ```
3. Đợi vài giây cho đến khi hiện dòng chữ `Started ChatbotApplication...`. Backend sẽ khởi động thành công và lắng nghe tại: **http://localhost:8080**

#### Bước 4.2. Khởi chạy Frontend (Next.js)
1. Mở một Terminal / CMD MỚI (để giữ terminal backend chạy ngầm).
2. Di chuyển vào thư mục `frontend/`:
   ```bash
   cd frontend
   ```
3. Cài đặt các gói thư viện cần thiết (Chỉ cần chạy lần đầu tiên):
   ```bash
   npm install
   ```
4. Khởi chạy ứng dụng web:
   ```bash
   npm run dev
   ```
5. Mở trình duyệt và truy cập: **http://localhost:3000** để đăng nhập và chat với bot.

---

### 5. Hướng dẫn Training / Fine-tuning AI (Tùy chọn)

Dự án cung cấp một module huấn luyện độc lập giúp bạn tự Fine-tune (huấn luyện thêm) các mô hình ngôn ngữ mã nguồn mở (như Qwen, LLaMA) bằng dữ liệu đặc thù của trường học. Quá trình này ứng dụng thư viện `Unsloth` giúp tối ưu tài nguyên, tăng tốc độ train và giảm lượng VRAM tiêu thụ đáng kể.

1. Mở terminal mới và di chuyển vào thư mục huấn luyện:
   ```bash
   cd finetuning
   ```
2. Cài đặt các thư viện Python chuyên biệt phục vụ quá trình Fine-tuning và Serve API:
   ```bash
   pip install "unsloth[colab-new] @ git+https://github.com/unslothai/unsloth.git"
   pip install --no-deps "trl<0.9.0" peft accelerate bitsandbytes
   pip install fastapi uvicorn sse_starlette pydantic python-multipart
   ```
3. **Huấn luyện mô hình:**
   Bạn có thể điều chỉnh dữ liệu đầu vào trong tập lệnh, sau đó tiến hành quá trình train:
   ```bash
   python train_qwen.py
   ```
   *Quá trình này có thể tốn một khoảng thời gian tuỳ vào GPU của máy bạn. Sau khi hoàn thành, mô hình (checkpoints) sẽ được xuất ra và lưu trữ ngay tại thư mục `finetuning/results`.*
4. **Khởi chạy Local AI Server:**
   Chạy API giả lập theo chuẩn OpenAI để phục vụ mô hình vừa huấn luyện:
   ```bash
   python api_server.py
   ```
   Server sẽ tự động nạp (load) checkpoint (Lora/Weights) từ thư mục `results` và mở cổng API tại địa chỉ **http://localhost:8000**.

*🌟 Mẹo nhỏ: Để sử dụng Local Model này vào hệ thống chính, trên giao diện Frontend của ứng dụng web, bạn có thể chuyển mô hình dự đoán sang chế độ "Local Fine-tuned Model". Backend lúc này sẽ tự động redirect câu hỏi của sinh viên về `localhost:8000` thay vì hỏi ngược lên máy chủ của Gemini.*

---

## 🛠 Cấu trúc dự án
- `/backend`: Mã nguồn Spring Boot, xử lý REST API, cơ chế RAG, nhúng Vector và kết nối LLM.
- `/frontend`: Mã nguồn giao diện Next.js, hiển thị Chat UI, Quản lý tài liệu và tích hợp Đăng nhập bằng Google.
- `/finetuning`: Các tập lệnh (scripts) Python để huấn luyện (Fine-Tuning) bằng dữ liệu tuỳ chỉnh trên máy cá nhân và cung cấp Local inference API.
