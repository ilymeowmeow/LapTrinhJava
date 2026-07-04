"use client";

import { useState } from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, CartesianGrid } from 'recharts';

interface EvaluationResult {
  ragResult: {
    answer: string;
    timeTakenMs: number;
    sources: any[];
  };
  baseResult: {
    answer: string;
    timeTakenMs: number;
  };
}

export default function ResearchDashboard() {
  const [query, setQuery] = useState("");
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [result, setResult] = useState<EvaluationResult | null>(null);

  const handleEvaluate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    setIsEvaluating(true);
    try {
      const res = await fetch("http://localhost:8080/api/evaluation/compare", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: query })
      });
      if (res.ok) {
        const data = await res.json();
        setResult(data);
      }
    } catch (e) {
      console.error(e);
      alert("Lỗi khi chạy đánh giá.");
    } finally {
      setIsEvaluating(false);
    }
  };

  const chartData = result ? [
    {
      name: 'Độ trễ (Latency)',
      RAG: result.ragResult.timeTakenMs,
      "Mô hình gốc (Mock Fine-tuned)": result.baseResult.timeTakenMs,
    }
  ] : [];

  return (
    <div className="max-w-7xl mx-auto h-full flex flex-col gap-6 pb-10 overflow-y-auto">
      <header className="pt-4">
        <h1 className="text-3xl font-bold text-slate-800 dark:text-slate-100 flex items-center gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-indigo-500"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><path d="M8 13h2"/><path d="M8 17h2"/><path d="M14 13h2"/><path d="M14 17h2"/></svg>
          Research Module (RBL)
        </h1>
        <p className="text-slate-500 mt-2">Đánh giá và so sánh hiệu năng giữa kiến trúc RAG và Mô hình gốc (Giả lập Fine-tuned) trong bối cảnh tiếng Việt.</p>
      </header>

      {/* Control Panel */}
      <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
        <h2 className="text-xl font-semibold mb-4">A/B Testing Thực nghiệm</h2>
        <form onSubmit={handleEvaluate} className="flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium mb-2 text-slate-700 dark:text-slate-300">Nhập câu hỏi để kiểm tra độ chính xác và ảo giác:</label>
            <input 
              type="text" 
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="VD: Hãy so sánh sự khác nhau giữa Array và Linked List?" 
              className="w-full bg-white/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-slate-800 dark:text-slate-100"
            />
          </div>
          <button 
            type="submit" 
            disabled={!query.trim() || isEvaluating}
            className="h-[50px] bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-medium rounded-xl px-6 shadow-lg shadow-indigo-500/30 transition-all disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {isEvaluating ? "Đang đánh giá..." : "Chạy thực nghiệm"}
          </button>
        </form>
      </div>

      {result && (
        <div className="flex flex-col xl:flex-row gap-6 mt-2">
          {/* Compare Results */}
          <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* RAG Column */}
            <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-t-4 border-indigo-500 shadow-xl rounded-3xl p-6 flex flex-col">
              <div className="flex items-center justify-between mb-4 border-b border-slate-100 dark:border-slate-700 pb-3">
                <h3 className="font-bold text-lg text-indigo-600 dark:text-indigo-400">Kiến trúc RAG (Gemini + VectorDB)</h3>
                <span className="text-xs font-mono bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 px-2 py-1 rounded">
                  {result.ragResult.timeTakenMs} ms
                </span>
              </div>
              <div className="prose prose-sm dark:prose-invert flex-1 overflow-y-auto max-h-[500px] pr-2 custom-scrollbar">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {result.ragResult.answer}
                </ReactMarkdown>
              </div>
              <div className="mt-4 pt-4 border-t border-slate-100 dark:border-slate-700">
                <p className="text-xs text-slate-500 mb-2 font-semibold">Tài liệu tham chiếu (Context retrieved): {result.ragResult.sources?.length || 0}</p>
                <div className="flex flex-wrap gap-1">
                  {result.ragResult.sources?.map((s, i) => (
                    <span key={i} className="text-[10px] bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 px-2 py-1 rounded border border-slate-200 dark:border-slate-600">
                      {s.filename}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            {/* Base Model Column */}
            <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-t-4 border-purple-500 shadow-xl rounded-3xl p-6 flex flex-col">
              <div className="flex items-center justify-between mb-4 border-b border-slate-100 dark:border-slate-700 pb-3">
                <h3 className="font-bold text-lg text-purple-600 dark:text-purple-400">Mô hình Gốc (No Context)</h3>
                <span className="text-xs font-mono bg-purple-50 dark:bg-purple-900/30 text-purple-700 px-2 py-1 rounded">
                  {result.baseResult.timeTakenMs} ms
                </span>
              </div>
              <div className="prose prose-sm dark:prose-invert flex-1 overflow-y-auto max-h-[500px] pr-2 custom-scrollbar">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {result.baseResult.answer}
                </ReactMarkdown>
              </div>
              <div className="mt-4 pt-4 border-t border-slate-100 dark:border-slate-700">
                <p className="text-xs text-slate-500 mb-2 font-semibold">Tài liệu tham chiếu:</p>
                <span className="text-[10px] bg-red-50 text-red-600 px-2 py-1 rounded border border-red-200 font-medium">
                  Không sử dụng tài liệu (Dễ bị ảo giác / Hallucination)
                </span>
              </div>
            </div>
          </div>

          {/* Metrics Column */}
          <div className="w-full xl:w-80 flex flex-col gap-6">
            <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
              <h3 className="font-bold text-slate-800 dark:text-slate-200 mb-4">So sánh Độ trễ (Latency)</h3>
              <div className="h-48 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData} margin={{ top: 20, right: 0, left: -20, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                    <XAxis dataKey="name" tick={{fontSize: 12}} axisLine={false} tickLine={false} />
                    <YAxis tick={{fontSize: 12}} axisLine={false} tickLine={false} />
                    <Tooltip cursor={{fill: 'rgba(0,0,0,0.05)'}} contentStyle={{borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'}} />
                    <Legend wrapperStyle={{fontSize: '12px', paddingTop: '10px'}} />
                    <Bar dataKey="RAG" fill="#6366f1" radius={[4, 4, 0, 0]} barSize={40} />
                    <Bar dataKey="Mô hình gốc (Mock Fine-tuned)" fill="#a855f7" radius={[4, 4, 0, 0]} barSize={40} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
              <div className="mt-4 text-xs text-slate-500">
                <p><strong>Nhận xét:</strong> RAG thường có độ trễ cao hơn do phải tốn thời gian truy vấn Vector Database (Qdrant/pgvector) trước khi sinh văn bản.</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}