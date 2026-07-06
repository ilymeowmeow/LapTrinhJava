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

interface ChunkingResult {
  [key: string]: {
    name: string;
    timeMs: number;
    chunkCount: number;
    chunks: string[];
  }
}

export default function ResearchDashboard() {
  const [activeTab, setActiveTab] = useState<"rag" | "chunking" | "embedding">("rag");

  // RAG vs Fine-tuned State
  const [query, setQuery] = useState("");
  const [localEndpoint, setLocalEndpoint] = useState("http://localhost:8001/api/generate");
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [result, setResult] = useState<EvaluationResult | null>(null);

  // Chunking State
  const [chunkText, setChunkText] = useState("");
  const [isChunking, setIsChunking] = useState(false);
  const [chunkResult, setChunkResult] = useState<ChunkingResult | null>(null);

  // Embedding State
  const [embeddingText, setEmbeddingText] = useState("");
  const [isEmbedding, setIsEmbedding] = useState(false);
  const [embeddingResult, setEmbeddingResult] = useState<any | null>(null);

  const handleEvaluate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    setIsEvaluating(true);
    try {
      const res = await fetch("http://localhost:8080/api/evaluation/compare", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: query, localEndpoint: localEndpoint })
      });
      if (res.ok) {
        const data = await res.json();
        setResult(data);
      }
    } catch (e) {
      console.error(e);
      alert("Lỗi khi chạy đánh giá RAG.");
    } finally {
      setIsEvaluating(false);
    }
  };

  const handleChunkingBenchmark = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!chunkText.trim()) return;

    setIsChunking(true);
    try {
      const res = await fetch("http://localhost:8080/api/evaluation/chunking", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text: chunkText })
      });
      if (res.ok) {
        const data = await res.json();
        setChunkResult(data);
      }
    } catch (e) {
      console.error(e);
      alert("Lỗi khi benchmark chunking.");
    } finally {
      setIsChunking(false);
    }
  };

  const handleEmbeddingBenchmark = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!embeddingText.trim()) return;

    setIsEmbedding(true);
    try {
      const res = await fetch("http://localhost:8080/api/evaluation/embedding", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text: embeddingText })
      });
      if (res.ok) {
        const data = await res.json();
        setEmbeddingResult(data);
      }
    } catch (e) {
      console.error(e);
      alert("Lỗi khi benchmark embedding.");
    } finally {
      setIsEmbedding(false);
    }
  };

  const chartData = result ? [
    {
      name: 'Độ trễ (Latency)',
      RAG: result.ragResult.timeTakenMs,
      "Fine-Tuned": result.baseResult.timeTakenMs,
    }
  ] : [];

  return (
    <div className="max-w-7xl mx-auto h-screen flex flex-col gap-6 pb-10 overflow-y-auto custom-scrollbar pr-2 px-4">
      <header className="pt-4">
        <h1 className="text-3xl font-bold text-slate-800 dark:text-slate-100 flex items-center gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-indigo-500"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><path d="M8 13h2"/><path d="M8 17h2"/><path d="M14 13h2"/><path d="M14 17h2"/></svg>
          Research Module (RBL)
        </h1>
        <p className="text-slate-500 mt-2">Bảng điều khiển thực nghiệm đánh giá mô hình ngôn ngữ và xử lý văn bản.</p>
        
        {/* Tabs */}
        <div className="flex gap-4 mt-6 border-b border-slate-200 dark:border-slate-700 pb-2">
          <button 
            onClick={() => setActiveTab("rag")}
            className={`px-4 py-2 font-medium rounded-t-lg transition-colors ${activeTab === "rag" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-slate-500 hover:text-slate-700"}`}
          >
            1. RAG vs Fine-Tuned Model
          </button>
          <button 
            onClick={() => setActiveTab("chunking")}
            className={`px-4 py-2 font-medium rounded-t-lg transition-colors ${activeTab === "chunking" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-slate-500 hover:text-slate-700"}`}
          >
            2. Chunking Strategies
          </button>
          <button 
            onClick={() => setActiveTab("embedding")}
            className={`px-4 py-2 font-medium rounded-t-lg transition-colors ${activeTab === "embedding" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-slate-500 hover:text-slate-700"}`}
          >
            3. Embedding Benchmark
          </button>
        </div>
      </header>

      {/* TAB 1: RAG VS BASE */}
      {activeTab === "rag" && (
        <div className="flex flex-col gap-6 animate-in fade-in">
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
            <h2 className="text-xl font-semibold mb-4">A/B Testing Thực nghiệm</h2>
            <form onSubmit={handleEvaluate} className="flex flex-col gap-4">
              <div className="flex gap-4 w-full">
                <div className="flex-1">
                  <label className="block text-sm font-medium mb-2 text-slate-700 dark:text-slate-300">Local Endpoint (Fine-tuned Model):</label>
                  <input 
                    type="text" 
                    value={localEndpoint}
                    onChange={(e) => setLocalEndpoint(e.target.value)}
                    placeholder="http://localhost:8001/api/generate" 
                    className="w-full bg-white/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-slate-800 dark:text-slate-100 text-sm"
                  />
                </div>
              </div>
              <div className="flex gap-4 items-end">
                <div className="flex-1">
                  <label className="block text-sm font-medium mb-2 text-slate-700 dark:text-slate-300">Nhập câu hỏi để kiểm tra độ chính xác và ảo giác:</label>
                  <input 
                    type="text" 
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="VD: Hãy giải thích cây nhị phân tìm kiếm?" 
                    className="w-full bg-white/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-slate-800 dark:text-slate-100"
                  />
                </div>
                <button 
                  type="submit" 
                  disabled={!query.trim() || isEvaluating}
                  className="h-[50px] bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-medium rounded-xl px-6 shadow-lg shadow-indigo-500/30 transition-all disabled:opacity-50 flex items-center justify-center gap-2"
                >
                  {isEvaluating ? "Đang chạy..." : "Chạy A/B Benchmark"}
                </button>
              </div>
            </form>
          </div>

          {result && (
            <div className="flex flex-col xl:flex-row gap-6 mt-2">
              <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* RAG Column */}
                <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-t-4 border-indigo-500 shadow-xl rounded-3xl p-6 flex flex-col">
                  <div className="flex items-center justify-between mb-4 border-b border-slate-100 dark:border-slate-700 pb-3">
                    <h3 className="font-bold text-lg text-indigo-600 dark:text-indigo-400">Kiến trúc RAG (Có VectorDB)</h3>
                    <span className="text-xs font-mono bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 px-2 py-1 rounded">
                      {result.ragResult.timeTakenMs} ms
                    </span>
                  </div>
                  <div className="prose prose-sm dark:prose-invert flex-1 overflow-y-auto max-h-[500px] pr-2 custom-scrollbar">
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{result.ragResult.answer}</ReactMarkdown>
                  </div>
                  <div className="mt-4 pt-4 border-t border-slate-100 dark:border-slate-700">
                    <p className="text-xs text-slate-500 mb-2 font-semibold">Tài liệu tham chiếu (Context retrieved): {result.ragResult.sources?.length || 0} đoạn văn (chunks)</p>
                    <div className="flex flex-wrap gap-1">
                      {Array.from(new Set(result.ragResult.sources?.map((s: any) => s.filename))).map((filename: any, i) => (
                        <span key={i} className="text-[10px] bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300 px-2 py-1 rounded border border-slate-200 dark:border-slate-600">
                          {filename} ({result.ragResult.sources?.filter((s: any) => s.filename === filename).length} đoạn)
                        </span>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Fine-Tuned Model Column */}
                <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-t-4 border-purple-500 shadow-xl rounded-3xl p-6 flex flex-col">
                  <div className="flex items-center justify-between mb-4 border-b border-slate-100 dark:border-slate-700 pb-3">
                    <h3 className="font-bold text-lg text-purple-600 dark:text-purple-400">Mô hình Fine-Tuned (Local)</h3>
                    <span className="text-xs font-mono bg-purple-50 dark:bg-purple-900/30 text-purple-700 px-2 py-1 rounded">
                      {result.baseResult.timeTakenMs} ms
                    </span>
                  </div>
                  <div className="prose prose-sm dark:prose-invert flex-1 overflow-y-auto max-h-[500px] pr-2 custom-scrollbar">
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{result.baseResult.answer}</ReactMarkdown>
                  </div>
                  <div className="mt-4 pt-4 border-t border-slate-100 dark:border-slate-700">
                    <p className="text-xs text-slate-500 mb-2 font-semibold">Đặc điểm:</p>
                    <span className="text-[10px] bg-blue-50 text-blue-600 px-2 py-1 rounded border border-blue-200 font-medium">
                      Sử dụng kiến thức nội tại (đã fine-tune), không truy xuất VectorDB.
                    </span>
                  </div>
                </div>
              </div>

              {/* Metrics */}
              <div className="w-full xl:w-80 flex flex-col gap-6">
                <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
                  <h3 className="font-bold text-slate-800 dark:text-slate-200 mb-4">Độ trễ (Latency)</h3>
                  <div className="h-48 w-full">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={chartData} margin={{ top: 20, right: 0, left: -20, bottom: 0 }}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                        <XAxis dataKey="name" tick={{fontSize: 12}} axisLine={false} tickLine={false} />
                        <YAxis tick={{fontSize: 12}} axisLine={false} tickLine={false} />
                        <Tooltip cursor={{fill: 'rgba(0,0,0,0.05)'}} shared={false} formatter={(value: number, name: string) => [`${value} ms`, name]} />
                        <Legend wrapperStyle={{fontSize: '12px'}} />
                        <Bar dataKey="RAG" fill="#6366f1" radius={[4, 4, 0, 0]} />
                        <Bar dataKey="Fine-Tuned" fill="#a855f7" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </div>
                  <div className="mt-4 text-xs text-slate-500 leading-relaxed">
                    <strong>Nhận xét:</strong> {result.ragResult.timeTakenMs > result.baseResult.timeTakenMs 
                      ? "RAG tốn nhiều thời gian hơn do phải chờ bước truy vấn VectorDB và nhồi thêm Context vào Prompt gửi cho LLM."
                      : "Mô hình Fine-Tuned (Local) xử lý chậm hơn RAG do phần cứng máy tính cục bộ chạy suy luận (inference) tốn nhiều thời gian hơn so với tốc độ phản hồi siêu tốc của Cloud API (mặc dù RAG phải tốn thêm bước tìm kiếm VectorDB)."}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: CHUNKING BENCHMARK */}
      {activeTab === "chunking" && (
        <div className="flex flex-col gap-6 animate-in fade-in">
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
            <h2 className="text-xl font-semibold mb-4">Benchmark Thuật toán Cắt văn bản (Text Splitters)</h2>
            <form onSubmit={handleChunkingBenchmark} className="flex flex-col gap-4">
              <div>
                <label className="block text-sm font-medium mb-2 text-slate-700 dark:text-slate-300">Nhập một đoạn văn bản dài để kiểm tra cách cắt:</label>
                <textarea 
                  value={chunkText}
                  onChange={(e) => setChunkText(e.target.value)}
                  rows={4}
                  placeholder="Nhập nội dung văn bản..." 
                  className="w-full bg-white/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-slate-800 dark:text-slate-100 resize-none"
                />
              </div>
              <div className="flex justify-end">
                <button 
                  type="submit" 
                  disabled={!chunkText.trim() || isChunking}
                  className="bg-emerald-600 hover:bg-emerald-500 text-white font-medium rounded-xl px-6 py-2 shadow-lg shadow-emerald-500/30 transition-all disabled:opacity-50"
                >
                  {isChunking ? "Đang xử lý..." : "Chạy Benchmark Splitters"}
                </button>
              </div>
            </form>
          </div>

          {chunkResult && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {Object.values(chunkResult).map((algo, i) => (
                <div key={i} className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
                  <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-700 pb-3 mb-4">
                    <h3 className="font-bold text-slate-800 dark:text-slate-100">{algo.name}</h3>
                    <span className="text-xs bg-slate-100 dark:bg-slate-700 px-2 py-1 rounded text-slate-600 dark:text-slate-300">{algo.timeMs} ms</span>
                  </div>
                  <div className="mb-4">
                    <span className="text-sm font-semibold text-indigo-600">Tổng số chunk: {algo.chunkCount}</span>
                  </div>
                  <div className="flex flex-col gap-3 max-h-[400px] overflow-y-auto custom-scrollbar pr-2">
                    {algo.chunks.map((chunk, idx) => (
                      <div key={idx} className="bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 p-3 rounded-lg text-sm text-slate-700 dark:text-slate-300 leading-relaxed">
                        <span className="font-bold text-indigo-500 mr-2">#{idx + 1}</span>
                        {chunk}
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: EMBEDDING BENCHMARK */}
      {activeTab === "embedding" && (
        <div className="flex flex-col gap-6 animate-in fade-in">
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6">
            <h2 className="text-xl font-semibold mb-4">Thực nghiệm Mô hình Nhúng Vector (Embedding Models)</h2>
            <form onSubmit={handleEmbeddingBenchmark} className="flex flex-col gap-4">
              <div>
                <label className="block text-sm font-medium mb-2 text-slate-700 dark:text-slate-300">Nhập văn bản cần Vector hóa để đo lường:</label>
                <textarea 
                  value={embeddingText}
                  onChange={(e) => setEmbeddingText(e.target.value)}
                  rows={3}
                  placeholder="Nhập nội dung cần phân tích..." 
                  className="w-full bg-white/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-700 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-slate-800 dark:text-slate-100 resize-none"
                />
              </div>
              <div className="flex justify-end">
                <button 
                  type="submit" 
                  disabled={!embeddingText.trim() || isEmbedding}
                  className="bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-xl px-6 py-2 shadow-lg shadow-indigo-500/30 transition-all disabled:opacity-50 flex items-center gap-2"
                >
                  {isEmbedding && <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>}
                  {isEmbedding ? "Đang xử lý (Lần đầu sẽ tốn xíu thời gian để nạp Model)..." : "Chạy Benchmark Embedding"}
                </button>
              </div>
            </form>
          </div>

          {embeddingResult && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {Object.values(embeddingResult).map((algo: any, i) => (
                <div key={i} className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border border-white/20 shadow-xl rounded-3xl p-6 flex flex-col gap-4">
                  <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-700 pb-3">
                    <div>
                      <h3 className="font-bold text-slate-800 dark:text-slate-100 text-lg">{algo.name}</h3>
                      <p className="text-xs text-slate-500">{algo.provider}</p>
                    </div>
                    <span className="text-sm font-mono bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 px-3 py-1 rounded-full font-bold shadow-sm border border-indigo-100">
                      {algo.timeMs} ms
                    </span>
                  </div>
                  
                  <div className="flex items-center justify-between bg-slate-50 dark:bg-slate-900/50 p-4 rounded-xl border border-slate-200 dark:border-slate-700">
                    <span className="text-sm font-medium text-slate-600 dark:text-slate-400">Số chiều (Dimensions):</span>
                    <span className="text-lg font-bold text-slate-800 dark:text-slate-100">{algo.dimensions}</span>
                  </div>

                  <div className="text-xs text-slate-500 mt-2">
                    {algo.name.includes("MiniLM") 
                      ? "Mô hình chạy Local bằng sức mạnh CPU của máy tính. Không tốn phí, an toàn dữ liệu nhưng số chiều thấp hơn, độ hiểu ngữ nghĩa bị giới hạn." 
                      : "Mô hình chạy trên máy chủ Google (Cloud). Trả về vector lớn 768 chiều cho độ chính xác RAG cao hơn, nhưng phụ thuộc mạng Internet."}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

    </div>
  );
}
