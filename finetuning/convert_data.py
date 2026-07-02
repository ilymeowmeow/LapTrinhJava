import json
import os

input_file = r"C:\UTH\Chatbot AI\finetuning\data\raw_data.md"
output_file = r"C:\UTH\Chatbot AI\finetuning\data\training_data.jsonl"

with open(input_file, "r", encoding="utf-8") as f:
    lines = f.readlines()

pairs = []
current_q = None
current_a = None

for line in lines:
    line = line.strip()
    if line.startswith("**Q:**"):
        current_q = line.replace("**Q:**", "").strip()
    elif line.startswith("**A:**"):
        current_a = line.replace("**A:**", "").strip()
        if current_q and current_a:
            pairs.append((current_q, current_a))
            current_q = None
            current_a = None

print(f"Tim thay {len(pairs)} cau hoi trong file Markdown.")

with open(output_file, "w", encoding="utf-8") as f:
    for q, a in pairs:
        text = f"<|im_start|>user\n{q}<|im_end|>\n<|im_start|>assistant\n{a}<|im_end|>"
        f.write(json.dumps({"text": text}, ensure_ascii=False) + "\n")

print(f"Da chuyen doi va luu thanh cong vao {output_file}")
