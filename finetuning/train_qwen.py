import os
import torch
from datasets import load_dataset
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    BitsAndBytesConfig,
    TrainingArguments
)
from peft import LoraConfig
from trl import SFTTrainer, SFTConfig

# 1. Configs
model_name = "Qwen/Qwen2.5-0.5B-Instruct"
dataset_name = "data/training_data.jsonl"
output_dir = "finetuned_model"

print("========================================")
print("  BAT DAU LOCAL FINE-TUNING VOI QLORA   ")
print("========================================")

# 2. BitsAndBytes 4-bit Config (Cho card 6GB VRAM)
bnb_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_compute_dtype=torch.float16,
    bnb_4bit_use_double_quant=True,
)

# 3. Load Model and Tokenizer
print("\n[1/5] Dang tai Tokenizer va Model (Qwen2.5-0.5B)...")
tokenizer = AutoTokenizer.from_pretrained(model_name)
tokenizer.pad_token = tokenizer.eos_token

model = AutoModelForCausalLM.from_pretrained(
    model_name,
    quantization_config=bnb_config,
    device_map="auto",
    torch_dtype=torch.float16
)
model.config.use_cache = False

# 4. LoRA Config
print("[2/5] Dang ap dung cau hinh LoRA...")
peft_config = LoraConfig(
    r=16,
    lora_alpha=32,
    lora_dropout=0.05,
    bias="none",
    task_type="CAUSAL_LM"
)

# 5. Load Dataset
print(f"[3/5] Dang tai tap du lieu tu {dataset_name}...")
dataset = load_dataset("json", data_files=dataset_name, split="train")

def format_prompt(example):
    return {"text": example["text"]}

dataset = dataset.map(format_prompt)

# 6. Training Arguments (Toi uu cho laptop)
print("[4/5] Chuan bi tham so huan luyen...")
training_args = SFTConfig(
    output_dir=output_dir,
    per_device_train_batch_size=1,     # Bat buoc la 1 de khong OOM
    gradient_accumulation_steps=4,     # Gom 4 batch moi cap nhat gradient
    learning_rate=2e-4,
    logging_steps=5,
    num_train_epochs=30,               # Doc lai tai lieu 30 lan de thuoc long
    optim="paged_adamw_8bit",
    fp16=False,
    bf16=False,
    save_steps=25,
    report_to="none",
    dataset_text_field="text",
    max_length=512,
)

# 7. SFT Trainer
trainer = SFTTrainer(
    model=model,
    train_dataset=dataset,
    peft_config=peft_config,
    processing_class=tokenizer,
    args=training_args,
)

# 8. Train
print("\n[5/5] Dang bat dau huan luyen (Training)...")
trainer.train(resume_from_checkpoint=True)

# 9. Save
print("\n[HOAN THANH] Dang luu mo hinh vao thu muc", output_dir)
trainer.model.save_pretrained(output_dir)
tokenizer.save_pretrained(output_dir)
print("Tuyet voi! Ban da Fine-tune thanh cong tren Laptop.")
