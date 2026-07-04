import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import SessionProviderWrapper from "@/components/SessionProviderWrapper";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Chatbot RBL - Hỗ trợ sinh viên",
  description: "Hệ thống hỏi đáp tài liệu môn học và thực nghiệm RAG",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="vi">
      <body className={`${inter.className} overflow-hidden`}>
        <SessionProviderWrapper>
          {children}
        </SessionProviderWrapper>
      </body>
    </html>
  );
}