@echo off
echo ==============================================
echo   KHOI TAO MOI TRUONG FINE-TUNING AI LOCAL
echo ==============================================
echo.

IF NOT EXIST "env" (
    echo [1] Dang tao moi truong ao Python venv ...
    "C:\Users\huynh thanh phat\miniconda3\python.exe" -m venv env
) ELSE (
    echo [1] Moi truong ao venv da ton tai.
)

echo [2] Cai dat PyTorch ho tro CUDA Nvidia GPU ...
echo Xin cho mot chut, buoc nay tai khoang 2.5GB...
.\env\Scripts\python.exe -m pip install torch --index-url https://download.pytorch.org/whl/cu124

echo.
echo [3] Cai dat cac thu vien AI Transformers, Peft, BitsAndBytes ...
.\env\Scripts\python.exe -m pip install transformers peft trl datasets accelerate bitsandbytes scipy

echo.
echo [4] Bat dau Fine-tuning!
.\env\Scripts\python.exe train_qwen.py

echo.
echo ==============================================
echo HOAN THANH! Ban co the tat cua so nay.
pause
