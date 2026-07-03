import Link from "next/link";

export default function Sidebar() {
  return (
    <div className="w-64 bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border-r border-slate-200 dark:border-slate-700 p-6 flex flex-col h-full shadow-lg transition-all">
      <div className="mb-8 flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-xl shadow-md">
          R
        </div>
        <div>
          <h1 className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400">
            Chatbot AI Education
          </h1>
          <p className="text-xs text-slate-500 font-medium">Hệ thống Hỏi đáp</p>
        </div>
      </div>

      <nav className="flex-col flex gap-2 flex-1">
        <Link 
          href="/" 
          className="flex items-center gap-3 px-4 py-3 text-slate-700 dark:text-slate-300 hover:bg-indigo-50 dark:hover:bg-slate-700/50 rounded-xl transition-all font-medium hover:text-indigo-600 dark:hover:text-indigo-400"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          Dashboard
        </Link>
        <Link 
          href="/documents" 
          className="flex items-center gap-3 px-4 py-3 text-slate-700 dark:text-slate-300 hover:bg-indigo-50 dark:hover:bg-slate-700/50 rounded-xl transition-all font-medium hover:text-indigo-600 dark:hover:text-indigo-400"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><line x1="10" x2="8" y1="9" y2="9"/></svg>
          Quản lý tài liệu
        </Link>
        <Link 
          href="/chat" 
          className="flex items-center gap-3 px-4 py-3 text-slate-700 dark:text-slate-300 hover:bg-indigo-50 dark:hover:bg-slate-700/50 rounded-xl transition-all font-medium hover:text-indigo-600 dark:hover:text-indigo-400"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m3 21 1.9-5.7a8.5 8.5 0 1 1 3.8 3.8z"/></svg>
          Chatbot Hỏi đáp
        </Link>
        <Link 
          href="/research" 
          className="flex items-center gap-3 px-4 py-3 text-slate-700 dark:text-slate-300 hover:bg-indigo-50 dark:hover:bg-slate-700/50 rounded-xl transition-all font-medium hover:text-indigo-600 dark:hover:text-indigo-400"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><path d="M8 13h2"/><path d="M8 17h2"/><path d="M14 13h2"/><path d="M14 17h2"/></svg>
          Module Nghiên cứu
        </Link>
      </nav>

      <div className="mt-auto pt-6 border-t border-slate-200 dark:border-slate-700">
        <div className="flex items-center gap-3 px-2">
          <div className="w-8 h-8 rounded-full bg-slate-200 dark:bg-slate-700"></div>
          <div className="text-sm">
            <p className="font-semibold text-slate-800 dark:text-slate-200">Sinh Viên</p>
            <p className="text-xs text-slate-500">ĐH KHTN</p>
          </div>
        </div>
      </div>
    </div>
  );
}
