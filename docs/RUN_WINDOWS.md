# Chạy dự án trên Windows

Tài liệu này áp dụng cho source hiện tại của repository `LapTrinhJava`.

## 1. Yêu cầu

- Java 17 và Maven.
- Node.js 18 trở lên và npm.
- Python 3.10 trở lên nếu chạy local fine-tuned model.
- Docker Desktop.

Kiểm tra:

```powershell
java -version
mvn -version
node --version
npm --version
python --version
docker --version
```

## 2. Biến môi trường

Backend dùng Spring AI OpenAI-compatible client để gọi Google Gemini. Vì vậy
key cần đặt bằng property chuẩn của Spring AI:

```powershell
$env:SPRING_AI_OPENAI_API_KEY="YOUR_GEMINI_API_KEY"
```

Đây là **Gemini API key**, không phải OpenAI API key. Không ghi key thật vào
`application.yml` hoặc commit lên Git.

Nếu Avast/antivirus thay chứng chỉ HTTPS và Java báo `PKIX path building
failed`, cho Java dùng Windows certificate store trong terminal chạy backend:

```powershell
$env:JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
```

Cách này vẫn giữ xác minh TLS, không sử dụng trust-all.

## 3. Khởi động PostgreSQL và PGVector

Tại thư mục gốc project:

```powershell
docker compose up -d
docker compose ps
```

Cấu hình hiện tại:

- Host: `localhost:5432`
- Database: `rag_db`
- Username: `postgres`
- Password: `postgres`

Không chạy `docker compose down -v` nếu muốn giữ dữ liệu.

## 4. Khởi động local fine-tuned model

Mở terminal thứ nhất:

```powershell
cd finetuning
pip install torch transformers peft accelerate fastapi uvicorn
python api_server.py
```

API chạy tại `http://localhost:8001/api/generate`. Source hiện chưa cung cấp
`/health`; có thể kiểm tra tài liệu FastAPI tại `http://localhost:8001/docs`.

Request mẫu:

```json
{
  "model": "qwen2.5:0.5b",
  "prompt": "Cây nhị phân tìm kiếm là gì?",
  "stream": false
}
```

## 5. Khởi động backend

Mở terminal thứ hai từ thư mục gốc:

```powershell
cd backend
mvn clean spring-boot:run
```

Backend chạy tại `http://localhost:8080`. Source hiện chưa có endpoint
`/api/health`; kiểm tra bằng `GET http://localhost:8080/api/documents`.

Khi gọi chế độ fine-tuned, frontend phải gửi:

```text
localModelEndpoint=http://localhost:8001/api/generate
```

## 6. Khởi động frontend

Mở terminal thứ ba:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Mở `http://localhost:3000`. Dùng `npm.cmd` thay cho `npm` nếu PowerShell báo
`npm.ps1 cannot be loaded because running scripts is disabled`.

> Lưu ý: checkout Git hiện tại phải có `frontend/package.json` và `frontend/src`
> mới chạy được frontend. Nếu hai mục này không tồn tại, cần lấy lại frontend
> từ branch/commit của thành viên phụ trách trước khi chạy.

## 7. Kiểm tra nhanh API

```powershell
Invoke-RestMethod http://localhost:8080/api/documents
```

Tạo session:

```powershell
$body = @{ title = "Demo" } | ConvertTo-Json
Invoke-RestMethod `
  -Uri http://localhost:8080/api/chat/session `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

Các URL chính:

- Backend: `http://localhost:8080`
- Fine-tuned API: `http://localhost:8001/docs`
- Frontend: `http://localhost:3000`
- Research UI: `http://localhost:3000/research` khi frontend tương ứng đã có.

## 8. Dừng hệ thống

Dừng backend, frontend và local model bằng `Ctrl+C` trong từng terminal. Sau đó:

```powershell
docker compose down
```

