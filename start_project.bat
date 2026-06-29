@echo off
echo ========================================
echo   KHOI DONG DU AN CHATBOT AI
echo ========================================

echo 1. Dang khoi dong Backend (Spring Boot)...
start "Backend (Java Spring Boot)" cmd /k "cd backend && ..\maven\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

echo 2. Dang khoi dong Frontend (Next.js)...
start "Frontend (Next.js)" cmd /k "cd frontend && npm run dev"

echo 3. Da gui lenh khoi dong! Vui long doi vai giay de cac cua so hien thi...
pause
