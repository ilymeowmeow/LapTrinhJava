"use client";

import { useState, useEffect, useRef } from "react";
import { useSession, signIn, signOut } from "next-auth/react";
import Image from "next/image";

export default function Home() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [activeView, setActiveView] = useState("chat"); // dashboard, documents, chat, research
  const [messages, setMessages] = useState<{role: string, content: string}[]>([]);
  const [inputValue, setInputValue] = useState("");
  const [selectedModel, setSelectedModel] = useState("RAG Mode");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [chatSubjectFilter, setChatSubjectFilter] = useState("");

  const [documents, setDocuments] = useState<any[]>([]);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadSubject, setUploadSubject] = useState("Nhập môn AI");
  const [uploadChapter, setUploadChapter] = useState("Chương 1");
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [isChatUploading, setIsChatUploading] = useState(false);
  const chatFileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (activeView === "documents") {
      fetchDocuments();
    }
  }, [activeView]);

  const fetchDocuments = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/documents");
      const data = await res.json();
      setDocuments(data);
    } catch (e) {
      console.error("Lỗi lấy danh sách tài liệu:", e);
    }
  };

  const handleDeleteDocument = async (id: number) => {
    if (!confirm("Bạn có chắc chắn muốn xóa tài liệu này? Mọi dữ liệu và Vector liên quan sẽ bị xóa khỏi Database.")) return;

    try {
      const res = await fetch(`http://localhost:8080/api/documents/${id}`, {
        method: "DELETE",
      });
      if (res.ok) {
        await fetchDocuments();
        alert("Đã xóa tài liệu thành công!");
      } else {
        alert("Lỗi khi xóa tài liệu!");
      }
    } catch (error) {
      alert("Lỗi kết nối đến server!");
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    const formData = new FormData();
    formData.append("file", file);
    formData.append("subject", uploadSubject);
    formData.append("chapter", uploadChapter);

    try {
      const res = await fetch("http://localhost:8080/api/documents/upload", {
        method: "POST",
        body: formData,
      });
      if (res.ok) {
        await fetchDocuments();
        alert("Tải lên và xử lý tài liệu thành công!");
      } else {
        alert("Lỗi khi tải tài liệu!");
      }
    } catch (error) {
      alert("Lỗi kết nối đến server!");
    } finally {
      setIsUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  const handleChatFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsChatUploading(true);
    const formData = new FormData();
    formData.append("file", file);
    formData.append("subject", file.name); // Sử dụng tên file làm subject
    formData.append("chapter", "Chat Upload");

    try {
      const res = await fetch("http://localhost:8080/api/documents/upload", {
        method: "POST",
        body: formData,
      });
      if (res.ok) {
        setChatSubjectFilter(file.name);
        alert("Đã đính kèm tài liệu thành công!");
      } else {
        alert("Lỗi khi đính kèm tài liệu!");
      }
    } catch (error) {
      alert("Lỗi kết nối đến server!");
    } finally {
      setIsChatUploading(false);
      if (chatFileInputRef.current) chatFileInputRef.current.value = "";
    }
  };

  const { data: session, status } = useSession();

  const handleSendMessage = async () => {
    if (!inputValue.trim()) return;
    const currentInput = inputValue;
    const currentMode = selectedModel;
    
    setMessages(prev => [...prev, { role: "user", content: currentInput }]);
    setInputValue("");
    
    try {
      const response = await fetch("http://localhost:8080/api/chat/ask/1", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          question: currentInput,
          mode: currentMode,
          subject: chatSubjectFilter
        }),
      });
      
      const data = await response.json();
      setMessages(prev => [...prev, { 
        role: "bot", 
        content: data.answer || "Không nhận được phản hồi từ hệ thống."
      }]);
    } catch (error) {
      setMessages(prev => [...prev, { 
        role: "bot", 
        content: `[Lỗi hệ thống] API Backend (Spring Boot) không phản hồi. Vui lòng kiểm tra server. Chi tiết: ${error}` 
      }]);
    }
  };

  if (status === "loading") {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F9FAFB]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#7C3AED]"></div>
      </div>
    );
  }

  if (status === "unauthenticated") {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F9FAFB] text-slate-800">
        <div className="bg-white p-10 rounded-3xl shadow-xl shadow-slate-200/50 max-w-md w-full text-center border border-slate-100">
          <div className="w-16 h-16 rounded-2xl bg-[#7C3AED] flex items-center justify-center text-white font-bold text-3xl mx-auto mb-6 shadow-sm">
            R
          </div>
          <h1 className="text-2xl font-bold text-slate-800 tracking-tight mb-2">Chào mừng đến Chatbot AI Education</h1>
          <p className="text-slate-500 mb-8 font-medium">Hệ thống trợ lý AI học tập thông minh.</p>
          <button
            onClick={() => signIn("google")}
            className="w-full flex items-center justify-center gap-3 bg-white border-2 border-slate-200 hover:border-slate-300 text-slate-700 font-bold py-3.5 px-4 rounded-2xl transition-all shadow-sm"
          >
            <svg viewBox="0 0 24 24" width="24" height="24" xmlns="http://www.w3.org/2000/svg">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
            </svg>
            Đăng nhập bằng Google
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-screen w-full font-sans bg-[#F9FAFB] text-slate-800">
      
      {/* Sidebar - Unified Simple Menu */}
      <aside className={`${isSidebarOpen ? 'w-[280px]' : 'w-0'} flex flex-col bg-[#FAFAFA] border-r border-slate-200/60 transition-all duration-300 overflow-hidden shrink-0 z-20`}>
        {/* Brand Logo */}
        <div className="flex items-center gap-3 p-6 mb-2">
          <div className="w-11 h-11 rounded-2xl bg-[#7C3AED] flex items-center justify-center text-white font-bold text-xl shadow-sm">
            R
          </div>
          <div>
            <div className="font-bold text-xl text-[#7C3AED] tracking-tight leading-tight">Chatbot AI Education</div>
            <div className="text-sm text-slate-500 font-medium">Hệ thống Hỏi đáp</div>
          </div>
        </div>

        {/* Unified Navigation Tabs */}
        <div className="flex-1 overflow-y-auto px-4 py-2 space-y-2">
          <button 
            onClick={() => setActiveView("documents")} 
            className={`w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl text-[15px] font-medium transition-all ${
              activeView === 'documents' ? 'bg-white shadow-sm border border-slate-200/60 text-slate-800' : 'text-slate-600 hover:bg-slate-100'
            }`}
          >
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" className="opacity-80">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
            Quản lý tài liệu
          </button>
          
          <button 
            onClick={() => setActiveView("chat")} 
            className={`w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl text-[15px] font-medium transition-all ${
              activeView === 'chat' ? 'bg-white shadow-sm border border-slate-200/60 text-slate-800' : 'text-slate-600 hover:bg-slate-100'
            }`}
          >
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" className="opacity-80">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            Chatbot Hỏi đáp
          </button>

          <button 
            onClick={() => setActiveView("research")} 
            className={`w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl text-[15px] font-medium transition-all ${
              activeView === 'research' ? 'bg-white shadow-sm border border-slate-200/60 text-slate-800' : 'text-slate-600 hover:bg-slate-100'
            }`}
          >
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" className="opacity-80">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="9" y1="15" x2="15" y2="15"/></svg>
            Module Nghiên cứu
          </button>
        </div>

        {/* User Info */}
        <div className="p-6 pb-14">
          <div className="flex items-center gap-3 p-2 bg-white rounded-2xl border border-slate-100 shadow-sm relative group cursor-pointer hover:border-slate-200 transition-colors">
            {session?.user?.image ? (
              <img src={session.user.image} alt="User Avatar" className="w-10 h-10 rounded-full object-cover shrink-0" />
            ) : (
              <div className="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white font-medium text-lg shrink-0">
                {session?.user?.name?.charAt(0) || "U"}
              </div>
            )}
            <div className="flex-1 overflow-hidden">
              <div className="text-[14px] font-bold text-slate-800 truncate">{session?.user?.name || "Người dùng"}</div>
              <div className="text-[12px] text-slate-500 font-medium truncate">{session?.user?.email || "Email trống"}</div>
            </div>
            
            {/* Logout button (appears on hover) */}
            <div onClick={() => signOut()} className="absolute right-3 opacity-0 group-hover:opacity-100 transition-opacity bg-white p-1.5 rounded-full hover:bg-slate-100 text-slate-400 hover:text-red-500" title="Đăng xuất">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col relative h-full bg-white">
        {/* Toggle Sidebar Button */}
        <div className="flex items-center p-4 absolute top-0 left-0 z-10">
          <button 
            onClick={() => setIsSidebarOpen(!isSidebarOpen)}
            className="p-2 bg-white shadow-sm border border-slate-200 hover:bg-slate-50 rounded-lg text-slate-600 transition-colors"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          </button>
        </div>

        {activeView === "chat" && (
          <div className="flex-1 flex flex-col items-center justify-center overflow-y-auto w-full relative">
            <input 
              type="file" 
              ref={chatFileInputRef} 
              onChange={handleChatFileUpload} 
              className="hidden" 
              accept=".pdf,.docx,.pptx,.txt" 
            />
            {messages.length === 0 ? (
              // Empty State
              <div className="flex-1 flex flex-col items-center justify-center w-full max-w-3xl px-6">
                <h1 className="text-3xl font-bold text-slate-800 mb-8 tracking-tight">
                  Trợ lý Hỏi đáp Chatbot AI Education
                </h1>
                
                {/* Input Area */}
                {chatSubjectFilter && (
                  <div className="w-full max-w-3xl mb-3 flex justify-start">
                    <div className="inline-flex items-center gap-2 bg-indigo-50 border border-indigo-100 text-indigo-700 px-3 py-1.5 rounded-xl text-sm font-medium">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
                      Lọc theo môn: {chatSubjectFilter}
                      <button 
                        onClick={() => setChatSubjectFilter("")}
                        className="ml-1 hover:bg-indigo-200 p-0.5 rounded-full transition-colors"
                      >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                      </button>
                    </div>
                  </div>
                )}
                <div className="w-full relative bg-white shadow-lg shadow-slate-200/50 rounded-3xl border border-slate-200/80 p-1 transition-all focus-within:border-[#7C3AED]/30">
                  <div className="flex items-end px-4 py-3 gap-3">
                    <button 
                      onClick={() => chatFileInputRef.current?.click()}
                      disabled={isChatUploading}
                      className="p-2 text-slate-400 hover:text-slate-600 transition-colors"
                      title="Đính kèm tài liệu"
                    >
                      {isChatUploading ? (
                        <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-slate-500"></div>
                      ) : (
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"></path></svg>
                      )}
                    </button>
                    <textarea 
                      value={inputValue}
                      onChange={(e) => setInputValue(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' && !e.shiftKey) {
                          e.preventDefault();
                          handleSendMessage();
                        }
                      }}
                      placeholder="Gõ câu hỏi về môn học..." 
                      className="flex-1 max-h-40 min-h-[44px] resize-none outline-none py-2 text-[16px] bg-transparent text-slate-800 placeholder:text-slate-400 overflow-y-auto"
                      rows={1}
                    />
                    
                    {/* Model Switcher Dropdown */}
                    <div className="relative flex items-center justify-center">
                      <button 
                        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                        className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-sm font-medium rounded-full transition-colors mr-2"
                      >
                        {selectedModel === "RAG Mode" ? "RAG" : "Fine-tune"}
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                      </button>
                      
                      {isDropdownOpen && (
                        <div className="absolute bottom-full right-0 mb-3 w-64 bg-[#1E1E1E] text-white rounded-2xl p-2 shadow-2xl z-50 animate-in fade-in zoom-in-95 duration-100 border border-slate-700">
                          <button 
                            onClick={() => { setSelectedModel("RAG Mode"); setIsDropdownOpen(false); }}
                            className="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800 rounded-xl transition-colors text-left group"
                          >
                            <div>
                              <div className="text-sm font-semibold text-white">RAG Mode</div>
                              <div className="text-xs text-slate-400 mt-0.5">Truy xuất kiến thức từ tài liệu PDF</div>
                            </div>
                            {selectedModel === "RAG Mode" && (
                              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-blue-400"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            )}
                          </button>
                          <button 
                            onClick={() => { setSelectedModel("Fine-tuning Mode"); setIsDropdownOpen(false); }}
                            className="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800 rounded-xl transition-colors text-left group mt-1"
                          >
                            <div>
                              <div className="text-sm font-semibold text-white">Fine-tune Mode</div>
                              <div className="text-xs text-slate-400 mt-0.5">Dùng kiến thức nội tại của LLM</div>
                            </div>
                            {selectedModel === "Fine-tuning Mode" && (
                              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-blue-400"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            )}
                          </button>
                        </div>
                      )}
                    </div>

                    <button 
                      onClick={handleSendMessage}
                      disabled={!inputValue.trim()}
                      className={`p-2.5 rounded-full transition-all flex items-center justify-center ${inputValue.trim() ? 'bg-black text-white hover:bg-slate-800' : 'bg-slate-100 text-slate-400'}`}
                    >
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
                    </button>
                  </div>
                </div>
              </div>
            ) : (
              // Active Chat State
              <div className="flex-1 flex flex-col w-full relative">
                <div className="flex-1 overflow-y-auto w-full pt-16 pb-40">
                  <div className="max-w-3xl mx-auto w-full flex flex-col space-y-8 px-6">
                    {messages.map((msg, index) => (
                      <div key={index} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                        {msg.role === 'bot' && (
                          <div className="w-10 h-10 rounded-full bg-[#7C3AED] flex items-center justify-center text-white shrink-0 mr-4 shadow-sm font-bold">
                            R
                          </div>
                        )}
                        <div className={`max-w-[85%] leading-relaxed ${
                          msg.role === 'user' 
                          ? 'bg-slate-100 text-slate-800 px-6 py-3.5 rounded-3xl rounded-tr-sm' 
                          : 'text-slate-800 py-1 text-[16px]'
                        }`}>
                          {msg.content}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
                
                {/* Fixed Input at Bottom */}
                <div className="absolute bottom-0 left-0 w-full bg-gradient-to-t from-white via-white to-transparent pt-10 pb-8">
                  <div className="max-w-3xl mx-auto px-6 w-full">
                    {chatSubjectFilter && (
                      <div className="w-full mb-3 flex justify-start">
                        <div className="inline-flex items-center gap-2 bg-indigo-50 border border-indigo-100 text-indigo-700 px-3 py-1.5 rounded-xl text-sm font-medium shadow-sm">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
                          Lọc theo môn: {chatSubjectFilter}
                          <button 
                            onClick={() => setChatSubjectFilter("")}
                            className="ml-1 hover:bg-indigo-200 p-0.5 rounded-full transition-colors"
                          >
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                          </button>
                        </div>
                      </div>
                    )}
                    <div className="w-full relative bg-white shadow-[0_-5px_20px_rgba(0,0,0,0.03)] rounded-3xl border border-slate-200/80 p-1">
                      <div className="flex items-end px-3 py-2 gap-3">
                        <button 
                          onClick={() => chatFileInputRef.current?.click()}
                          disabled={isChatUploading}
                          className="p-2.5 text-slate-400 hover:text-slate-600 transition-colors"
                          title="Đính kèm tài liệu"
                        >
                          {isChatUploading ? (
                            <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-slate-500"></div>
                          ) : (
                            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"></path></svg>
                          )}
                        </button>
                        <textarea 
                          value={inputValue}
                          onChange={(e) => setInputValue(e.target.value)}
                          onKeyDown={(e) => {
                            if (e.key === 'Enter' && !e.shiftKey) {
                              e.preventDefault();
                              handleSendMessage();
                            }
                          }}
                          placeholder="Gõ câu hỏi về môn học..." 
                          className="flex-1 max-h-40 min-h-[44px] resize-none outline-none py-3 text-[16px] bg-transparent text-slate-800 placeholder:text-slate-400 overflow-y-auto"
                          rows={1}
                        />
                        
                        {/* Model Switcher Dropdown for active chat */}
                        <div className="relative flex items-center justify-center">
                          <button 
                            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                            className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-sm font-medium rounded-full transition-colors mr-2"
                          >
                            {selectedModel === "RAG Mode" ? "RAG" : "Fine-tune"}
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                          </button>
                          
                          {isDropdownOpen && (
                            <div className="absolute bottom-full right-0 mb-3 w-64 bg-[#1E1E1E] text-white rounded-2xl p-2 shadow-2xl z-50 animate-in fade-in zoom-in-95 duration-100 border border-slate-700">
                              <button 
                                onClick={() => { setSelectedModel("RAG Mode"); setIsDropdownOpen(false); }}
                                className="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800 rounded-xl transition-colors text-left group"
                              >
                                <div>
                                  <div className="text-sm font-semibold text-white">RAG Mode</div>
                                  <div className="text-xs text-slate-400 mt-0.5">Truy xuất kiến thức từ tài liệu PDF</div>
                                </div>
                                {selectedModel === "RAG Mode" && (
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-blue-400"><polyline points="20 6 9 17 4 12"></polyline></svg>
                                )}
                              </button>
                              <button 
                                onClick={() => { setSelectedModel("Fine-tuning Mode"); setIsDropdownOpen(false); }}
                                className="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-800 rounded-xl transition-colors text-left group mt-1"
                              >
                                <div>
                                  <div className="text-sm font-semibold text-white">Fine-tune Mode</div>
                                  <div className="text-xs text-slate-400 mt-0.5">Dùng kiến thức nội tại của LLM</div>
                                </div>
                                {selectedModel === "Fine-tuning Mode" && (
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-blue-400"><polyline points="20 6 9 17 4 12"></polyline></svg>
                                )}
                              </button>
                            </div>
                          )}
                        </div>

                        <button 
                          onClick={handleSendMessage}
                          disabled={!inputValue.trim()}
                          className={`p-2.5 rounded-full transition-all flex items-center justify-center ${inputValue.trim() ? 'bg-black text-white hover:bg-slate-800' : 'bg-slate-100 text-slate-400'}`}
                        >
                          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Existing Views adapted to clean style */}
        {activeView === "documents" && (
          <div className="flex-1 overflow-y-auto pt-20 p-8 max-w-5xl mx-auto w-full">
            <h2 className="text-2xl font-bold text-slate-800 mb-8">Quản lý Tài liệu</h2>
            
            {/* Upload Box */}
            <div className="bg-white border border-slate-200 rounded-3xl p-8 mb-8 shadow-sm">
              <h3 className="text-lg font-bold text-slate-800 mb-4">Upload Tài liệu mới</h3>
              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="block text-sm font-medium text-slate-600 mb-2">Môn học</label>
                  <input type="text" value={uploadSubject} onChange={e => setUploadSubject(e.target.value)} className="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-[#7C3AED]" placeholder="Nhập môn học..." />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-600 mb-2">Chương</label>
                  <input type="text" value={uploadChapter} onChange={e => setUploadChapter(e.target.value)} className="w-full px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:border-[#7C3AED]" placeholder="Nhập chương..." />
                </div>
              </div>
              <input type="file" ref={fileInputRef} onChange={handleFileUpload} className="hidden" accept=".pdf,.docx,.pptx,.txt" />
              <div onClick={() => fileInputRef.current?.click()} className={`bg-[#FAFAFA] border-2 border-dashed border-slate-300 rounded-2xl p-10 text-center transition-colors cursor-pointer ${isUploading ? 'opacity-50 pointer-events-none' : 'hover:bg-slate-50'}`}>
                <div className="w-12 h-12 bg-white shadow-sm text-slate-400 rounded-xl flex items-center justify-center mx-auto mb-3 border border-slate-200">
                  {isUploading ? (
                    <div className="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-[#7C3AED]"></div>
                  ) : (
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                  )}
                </div>
                <p className="text-slate-700 font-semibold mb-1">{isUploading ? 'Đang tải lên và xử lý Vector...' : 'Chọn tài liệu (PDF, DOCX, PPTX)'}</p>
                <p className="text-slate-500 text-sm">Hệ thống sẽ tự động chunk và embed vào CSDL</p>
              </div>
            </div>

            {/* List Box */}
            <div className="bg-white border border-slate-200 rounded-3xl p-8 shadow-sm">
              <h3 className="text-lg font-bold text-slate-800 mb-4">Tài liệu đã Index</h3>
              {documents.length === 0 ? (
                <p className="text-slate-500">Chưa có tài liệu nào.</p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="border-b border-slate-200 text-slate-500 text-sm">
                        <th className="py-3 font-medium">Tên File</th>
                        <th className="py-3 font-medium">Môn học</th>
                        <th className="py-3 font-medium">Chương</th>
                        <th className="py-3 font-medium">Trạng thái</th>
                        <th className="py-3 font-medium">Ngày tải lên</th>
                        <th className="py-3 font-medium text-right">Hành động</th>
                      </tr>
                    </thead>
                    <tbody>
                      {documents.map((doc, i) => (
                        <tr key={i} className="border-b border-slate-100 last:border-0 text-slate-700 text-sm hover:bg-slate-50">
                          <td className="py-3 font-medium">{doc.filename}</td>
                          <td className="py-3">{doc.subject}</td>
                          <td className="py-3">{doc.chapter}</td>
                          <td className="py-3">
                            <span className={`px-2 py-1 rounded-full text-xs font-semibold ${doc.status === 'INDEXED' ? 'bg-green-100 text-green-700' : doc.status === 'FAILED' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'}`}>
                              {doc.status}
                            </span>
                          </td>
                          <td className="py-3 text-slate-500">{new Date(doc.uploadDate).toLocaleString('vi-VN')}</td>
                          <td className="py-3 text-right">
                            <div className="flex items-center justify-end gap-2">
                              {doc.status === 'INDEXED' && (
                                <button 
                                  onClick={() => {
                                    setChatSubjectFilter(doc.subject);
                                    setActiveView("chat");
                                  }}
                                  className="inline-flex items-center justify-center gap-1.5 px-3 py-1.5 bg-indigo-50 text-indigo-600 hover:bg-indigo-100 rounded-lg text-sm font-medium transition-colors"
                                  title="Chuyển sang hỏi đáp tài liệu này"
                                >
                                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                                  Hỏi đáp
                                </button>
                              )}
                              <button 
                                onClick={() => handleDeleteDocument(doc.id)}
                                className="inline-flex items-center justify-center p-1.5 bg-red-50 text-red-600 hover:bg-red-100 rounded-lg text-sm font-medium transition-colors"
                                title="Xóa tài liệu"
                              >
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 6h18"></path><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        )}

        {activeView === "research" && (
          <div className="flex-1 overflow-y-auto pt-20 p-8 max-w-5xl mx-auto w-full">
            <h2 className="text-2xl font-bold text-slate-800 mb-8">Module Nghiên cứu</h2>
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4">Cấu hình Evaluate Model</h3>
              <p className="text-slate-500 mb-6">Mô đun đánh giá mô hình LLM chuyên dụng bằng Java Backend.</p>
              <button className="px-6 py-2.5 bg-[#7C3AED] text-white font-medium rounded-xl hover:bg-[#6d28d9] transition-colors">
                Khởi chạy Benchmark
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
