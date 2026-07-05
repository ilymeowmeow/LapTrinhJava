#  Chatbot AI Education - Hệ Thống Trợ Lý Thông Minh cho Giáo Dục

Chatbot AI Education là một nền tảng trợ lý hỏi đáp thông minh được thiết kế để hỗ trợ sinh viên và giảng viên trong học tập. Hệ thống tích hợp hai chế độ hoạt động mạnh mẽ: **Chế độ RAG (Retrieval-Augmented Generation)** để truy xuất tài liệu học thuật và **Chế độ Fine-Tuning** để sử dụng kiến thức đã được tinh chỉnh, giúp cung cấp các câu trả lời chính xác và có ngữ cảnh.

---

##  Mục Lục

- [Giới Thiệu Dự Án](#giới-thiệu-dự-án)
- [Mô Tả Chi Tiết](#mô-tả-chi-tiết)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cách Cài Đặt và Khởi Chạy](#cách-cài-đặt-và-khởi-chạy)
- [Cách Sử Dụng Dự Án](#cách-sử-dụng-dự-án)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Lưu Ý Quan Trọng](#lưu-ý-quan-trọng)
- [Đóng góp](#Đóng-góp)

---

##  Giới Thiệu Dự Án

**Chatbot AI Education** là một hệ thống chatbot được phát triển bằng Java Spring Boot và Next.js, nhằm mục đích giúp sinh viên giải quyết các câu hỏi học thuật một cách nhanh chóng và chính xác. 

### Các Tính Năng Chính:
- **Trò chuyện Real-time** với AI được hỗ trợ bởi Gemini (Google) hoặc Llama 3 (Groq)
- **Chế độ RAG (Retrieval-Augmented Generation)**: Truy xuất tài liệu học thuật và cung cấp câu trả lời dựa trên ngữ cảnh
- **Chế độ Fine-Tuning**: Sử dụng mô hình AI được tinh chỉnh cho các bài toán cụ thể
- **Quản lý Tài Liệu**: Giảng viên có thể tải lên/xóa tài liệu học thuật
- **Lịch Sử Trò Chuyện**: Lưu giữ và quản lý các phiên trò chuyện
- **Kiến Trúc Vector Database**: Sử dụng PostgreSQL với extension PGVector để lưu trữ Embedding

---

## Mô Tả Chi Tiết

### Mục Đích Dự Án
Dự án được tạo ra để giải quyết các thách thức trong giáo dục hiện đại:
- **Hỗ trợ học tập 24/7**: Sinh viên có thể đặt câu hỏi bất cứ lúc nào mà không cần chờ giảng viên
- **Cá nhân hóa kiến thức**: Huấn luyện mô hình AI dựa trên tài liệu và nội dung cụ thể của từng khóa học
- **Giảm bớt tải công việc cho giảng viên**: Tự động hóa việc trả lời các câu hỏi thường gặp

### Tại Sao Chọn Công Nghệ Này?

| Công Nghệ | Lý Do Lựa Chọn |
|-----------|-------------|
| **Java Spring Boot** | Framework mạnh mẽ, ổn định, hỗ trợ microservices và dễ mở rộng |
| **Spring AI** | Tích hợp liền mạch với Gemini API và Groq, giảm độ phức tạp |
| **PostgreSQL + PGVector** | Cơ sở dữ liệu Vector hiệu năng cao, phù hợp cho ứng dụng RAG |
| **Next.js + React** | Frontend modern, hỗ trợ server-side rendering, tối ưu hiệu năng |
| **Tailwind CSS** | Styling hiệu quả, giúp xây dựng giao diện đẹp nhanh chóng |
| **Docker** | Đơn giản hóa quá trình triển khai, đảm bảo tính nhất quán giữa các môi trường |

### Những Thách Thức Gặp Phải & Giải Pháp

| Thách Thức | Giải Pháp |
|-----------|----------|
| **AI Hallucination** (AI bịa bỏ thông tin) | Sử dụng chế độ RAG để giới hạn AI chỉ trả lời dựa trên tài liệu có sẵn |
| **Bảo mật API Key** | Sử dụng Environment Variables, không hard-code key vào mã nguồn |
| **Hiệu năng tìm kiếm** | Sử dụng Vector Embedding + Cosine Similarity để tìm kiếm nhanh |
| **Khối lượng yêu cầu cao** | Thiết kế kiến trúc phân lớp (N-Tier) để dễ dàng scale-out |
| **Tính linh hoạt của AI** | Cung cấp cả chế độ RAG và Fine-Tuning để người dùng chọn phù hợp |

### Tính Năng Lên Kế Hoạch cho Tương Lai
-  **Hỗ trợ Đa Ngôn Ngữ**: Mở rộng hỗ trợ nhiều ngôn ngữ khác
-  **Xuất Báo Cáo**: Cho phép sinh viên xuất lịch sử trò chuyện thành PDF
-  **Tích hợp LMS**: Kết nối với các hệ thống quản lý học tập (Canvas, Moodle, Blackboard)
-  **Analytics & Dashboard**: Phân tích hành vi học tập của sinh viên
-  **Hỗ Trợ Voice**: Nhập câu hỏi bằng giọng nói

---

## Yêu Cầu Hệ Thống

Để chạy dự án này, máy tính của bạn cần cài đặt sẵn:

- **Java 17 hoặc cao hơn** - Dùng cho Spring Boot Backend
  - [Tải Java từ Oracle](https://www.oracle.com/java/technologies/downloads/#java17)
  
- **Node.js v18 hoặc v20+** - Dùng cho Next.js Frontend
  - [Tải Node.js](https://nodejs.org/)
  
- **Docker Desktop** - Dùng để chạy PostgreSQL với PGVector
  - [Tải Docker Desktop](https://www.docker.com/products/docker-desktop)
  
- **Git** - Để clone source code
  - [Tải Git](https://git-scm.com/)
  
- **Maven 3.8+** (tùy chọn) - Để build Java project
  - Có sẵn trong Spring Boot Wrapper, nhưng có thể cài riêng từ [Apache Maven](https://maven.apache.org/)

### Kiểm Tra Cài Đặt
```bash
# Kiểm tra Java
java -version

# Kiểm tra Node.js
node --version
npm --version

# Kiểm tra Docker
docker --version

# Kiểm tra Git
git --version
```

---

## Cách Cài Đặt và Khởi Chạy

### Bước 1: Clone Repository

```bash
git clone https://github.com/ilymeowmeow/LapTrinhJava.git
cd LapTrinhJava
```

### Bước 2: Thiết Lập Database (PostgreSQL + PGVector)

Hệ thống sử dụng PostgreSQL với extension Vector để lưu trữ dữ liệu và vector embedding.

1. Mở Terminal/CMD tại thư mục gốc dự án
2. Khởi động Database bằng Docker:
   ```bash
   docker-compose up -d
   ```
   Đợi Docker kéo image về và khởi chạy container. Database `rag_db` sẽ được tạo tự động.

3. Kiểm tra trạng thái container:
   ```bash
   docker-compose ps
   ```
   Bạn sẽ thấy `rag_db` đang chạy trên port `5432`.

### Bước 3: Cấu Hình API Key (Bảo Mật)

Dự án sử dụng Gemini (Google) và Llama 3 (Groq). **KHÔNG** nên hard-code API Key vào mã nguồn.

#### Trên Windows:
1. Nhấn `Windows + X`, chọn "System"
2. Chọn "Advanced system settings" → "Environment Variables"
3. Nhấn "New" dưới "User variables" và thêm:
   - **Variable name**: `OPENAI_API_KEY`
   - **Variable value**: [Điền API Key của Google Gemini]
   
4. Tương tự, thêm biến:
   - **Variable name**: `GROQ_API_KEY`
   - **Variable value**: [Điền API Key của Groq]

5. **Khởi động lại IDE hoặc Terminal** để biến môi trường có hiệu lực

#### Trên macOS/Linux:
```bash
# Thêm vào ~/.bash_profile hoặc ~/.zshrc
export OPENAI_API_KEY="your_gemini_api_key_here"
export GROQ_API_KEY="your_groq_api_key_here"

# Áp dụng thay đổi
source ~/.bash_profile
```

#### Hoặc sử dụng file `.env` (tùy chọn):
Tạo file `.env` ở thư mục `backend/`:
```
OPENAI_API_KEY=your_gemini_api_key_here
GROQ_API_KEY=your_groq_api_key_here
```

> **⚠️ Lưu ý**: Không commit file `.env` vào Git. Đã có trong `.gitignore`.

### Bước 4: Khởi Chạy Backend (Spring Boot)

1. Mở một Terminal/CMD mới
2. Di chuyển vào thư mục backend:
   ```bash
   cd backend
   ```
3. Chạy ứng dụng bằng Maven:
   ```bash
   mvn clean spring-boot:run
   ```
4. Đợi vài giây cho đến khi terminal hiển thị:
   ```
   Started ChatbotApplication in X.XXX seconds
   ```
5. Backend sẽ khởi chạy tại: **http://localhost:8080**

> **Tip**: Nếu gặp lỗi, kiểm tra:
> - Docker đã chạy và database sẵn sàng?
> - Environment variables đã được thiết lập?
> - Port 8080 có bị chiếm bởi ứng dụng khác?

### Bước 5: Khởi Chạy Frontend (Next.js)

1. Mở một Terminal/CMD **MỚI**, giữ nguyên terminal Backend đang chạy
2. Di chuyển vào thư mục frontend:
   ```bash
   cd frontend
   ```
3. Cài đặt các thư viện (chỉ cần chạy lần đầu):
   ```bash
   npm install
   ```
4. Khởi chạy ứng dụng Frontend:
   ```bash
   npm run dev
   ```
5. Mở trình duyệt và truy cập: **http://localhost:3000**

---

## Cách Sử Dụng Dự Án

### Cho Sinh Viên

#### 1. Tạo Phiên Trò Chuyện Mới
- Truy cập http://localhost:3000
- Nhấn "New Chat" hoặc "Tạo Phiên Mới"
- Hệ thống sẽ tạo một phiên chat riêng với ID duy nhất

#### 2. Đặt Câu Hỏi
- Gõ câu hỏi vào ô "Nhập câu hỏi của bạn..."
- Chọn chế độ:
  - **RAG Mode**: AI sử dụng tài liệu học thuật đã tải lên
  - **Fine-Tuning Mode**: AI sử dụng kiến thức đã được tinh chỉnh
- Nhấn "Gửi" hoặc Enter
- Chờ AI trả lời (thường mất 2-5 giây)

#### 3. Xem Lịch Sử Trò Chuyện
- Danh sách phiên trò chuyện hiển thị ở sidebar bên trái
- Nhấn vào bất kỳ phiên nào để xem lịch sử

### Cho Giảng Viên (Admin)

#### 1. Tải Lên Tài Liệu Học Thuật
- Truy cập phần "Documents" (Quản lý Tài Liệu)
- Nhấn "Upload Document"
- Chọn file PDF, TXT, hoặc DOCX từ máy tính
- Điền thông tin:
  - **Tên tài liệu**: Ví dụ "Bài giảng Java OOP"
  - **Môn học**: Ví dụ "Lập trình Java"
- Nhấn "Upload"
- Hệ thống sẽ tự động cắt tài liệu thành các chunks và tạo embedding vector

#### 2. Quản Lý Tài Liệu
- Xem danh sách tất cả tài liệu đã tải lên
- Nhấn nút "Delete" để xóa tài liệu không cần thiết
- Tài liệu đã xóa sẽ không còn được sử dụng trong chế độ RAG

#### 3. Huấn Luyện Mô Hình (Fine-Tuning)
- Truy cập phần "Fine-Tuning"
- Nhấn "Start Fine-Tuning"
- Chọn model: Gemini hoặc Llama 3
- Chọn tập dữ liệu từ thư mục `/finetuning/` (file JSON)
- Nhấn "Train"
- Hệ thống sẽ bắt đầu huấn luyện (có thể mất 5-30 phút)
- Xem tiến độ: Phần "Status" hiển thị tương lai độ hoàn thành

### Ví Dụ Sử Dụng

**Scenario 1: Sinh viên sử dụng RAG Mode**
```
Sinh viên: "Hãy giải thích về Design Pattern Factory trong Java?"
AI (RAG Mode): 
"Dựa trên tài liệu 'Design Patterns in Java', Factory Pattern là...
[Trích xuất từ tài liệu + tổng hợp]"
```

**Scenario 2: Giảng viên sử dụng Fine-Tuning Mode**
```
Giảng viên: "Tôi muốn hệ thống trả lời các câu hỏi dựa trên bài giảng riêng"
→ Tải lên bài giảng (PDF)
→ Khởi chạy Fine-Tuning
→ Sinh viên đặt câu hỏi → AI trả lời dựa trên bài giảng
```

---

## Cấu Trúc Dự Án

```
LapTrinhJava/
├── backend/                      # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/chatbot/
│   │   │   │       ├── controller/    # REST API Controllers
│   │   │   │       ├── service/       # Business Logic
│   │   │   │       ├── repository/    # Data Access Layer
│   │   │   │       └── entity/        # Database Entities
│   │   │   └── resources/
│   │   │       └── application.yml    # Spring Boot Configuration
│   │   └── test/                 # Unit & Integration Tests
│   ├── pom.xml                   # Maven Dependencies
│   └── mvnw / mvnw.cmd           # Maven Wrapper
│
├── frontend/                     # Next.js Frontend
│   ├── src/
│   │   ├── app/                  # Next.js App Router
│   │   ├── components/           # React Components
│   │   ├── lib/                  # Utility Functions
│   │   └── styles/               # Tailwind CSS Styles
│   ├── public/                   # Static Assets
│   ├── package.json
│   ├── next.config.ts
│   └── tsconfig.json
│
├── finetuning/                   # Fine-Tuning Scripts & Data
│   ├── dataset/                  # Training Datasets (JSON)
│   └── scripts/                  # Python Scripts
│
├── scripts/                      # Utility Scripts
│   └── test_gemini.py           # Test Gemini API Connection
│
├── docker-compose.yml            # Docker Compose Configuration
├── .gitignore
├── .cursorrules                  # AI Coding Rules
├── README.md                     # Original README (Vietnamese)
└── Phân tích và Thiết kế Hệ thống Chatbot AI.md  # Technical Design Document
```

### Mô Tả Các Thư Mục Chính

| Thư Mục | Mô Tả |
|---------|-------|
| **backend/** | Chứa toàn bộ mã nguồn Spring Boot, xử lý API, logic RAG, kết nối database |
| **frontend/** | Giao diện web Next.js, quản lý state, gọi API backend |
| **finetuning/** | Tập dữ liệu, script Python để huấn luyện mô hình AI |
| **scripts/** | Các script utility, như test kết nối API |
| **.cursorrules** | Quy tắc lập trình cho AI agents (Cursor, Copilot) |

---

## Công Nghệ Sử Dụng

### Backend Stack
```
Java 17
├── Spring Boot 3.x
├── Spring AI (Gemini & Groq Integration)
├── Spring Data JPA
├── PostgreSQL + PGVector
└── Maven
```

### Frontend Stack
```
Node.js v18+
├── Next.js 15+
├── React 18+
├── TypeScript
├── Tailwind CSS
└── npm/npx
```

### Infrastructure
```
Docker & Docker Compose
└── PostgreSQL 15+ (với pgvector extension)
```

### External APIs
```
Google Gemini API
├── gemini-robotics-er-1.6-preview (Chat Model)
└── gemini-embedding-2 (Embedding Model)

Groq API
└── llama-3.1-8b-instant (Chat Model)
```

---

## Lưu Ý Quan Trọng

### Bảo Mật
- **Không bao giờ commit API Key vào Git**. Luôn sử dụng Environment Variables.
- Giữ file `.env` ngoài source control (đã được thêm vào `.gitignore`).
- Nếu vô tình công khai API Key, hãy đổi key ngay lập tức trên các nền tảng cung cấp.

### Kiến Trúc Code
- Dự án sử dụng file `.cursorrules` để hướng dẫn AI agents tuân theo kiến trúc:
  - **Không** hard-code API Keys
  - **Không** phá vỡ cấu trúc N-Tier (Controller → Service → Repository)
  - **Không** xóa hoặc sửa đổi `.cursorrules`

### Hiệu Năng
- **RAG Mode**: Tìm kiếm và truy xuất tài liệu (2-3 giây)
- **Fine-Tuning Mode**: Chỉ sử dụng kiến thức mô hình (1-2 giây)
- Nếu hệ thống chậm, kiểm tra:
  - Docker đã sẵn sàng?
  - Network connection ổn định?
  - API quota của Gemini/Groq còn đủ?

### Database
- PostgreSQL sẽ tự động tạo database `rag_db` khi container khởi chạy lần đầu
- Để reset database: `docker-compose down -v` (xóa toàn bộ dữ liệu)
- Để xem dữ liệu trong database: Sử dụng pgAdmin hoặc psql CLI

---

##  Đóng góp

Dự án này được phát triển bởi:

| Thành Viên            | Vai Trò                                   | GitHub                                        |
|-----------------------|-------------------------------------------|-----------------------------------------------|
| **Đỗ Thiên Phúc**     | Backend Development                       | (https://github.com/ilymeowmeow) |
| **Huỳnh Thành Phát**  | Lead AI Development + Backend Development | (https://github.com/thanhphatblue2104-glitch)                                        |
| **Huỳnh Lê Bảo Trâm** | Frontend Development + AI Assistant       | (https://github.com/nonamemiurin-png)                                  |
| **Hà Hữu Tường**      | Backend Development                       | (https://github.com/tuonghh2477-dot)                                   |
| **Dương Đình Khôi**   | Frontend Development + AI Assistant       | (https://github.com/khoidd1318-247)                                   |

### Cảm Ơn Đặc Biệt
- Nhóm hỗ trợ từ **Google Gemini** và **Groq** vì cung cấp API mạnh mẽ
- Cộng đồng **Spring Boot** và **Next.js** vì tài liệu chi tiết
- Thầy **Nguyễn Văn Chiến** đã hướng dẫn và phản hồi

---

##  Tài Liệu Bổ Sung

- [Tài liệu Kiến Trúc Hệ Thống (UML)](./Phân%20tích%20và%20Thiết%20kế%20Hệ%20thống%20Chatbot%20AI.md)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [Next.js Documentation](https://nextjs.org/docs)
- [PostgreSQL + pgvector Guide](https://github.com/pgvector/pgvector)

---

**Cảm ơn bạn đã quan tâm đến dự án Chatbot AI Education! Happy Coding! 

*Cập nhật lần cuối: Tháng 7 năm 2026*
