package com.example.chatbot.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DocumentParserService {

    public String parsePdf(String filePath) throws IOException {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public String parseDocx(String filePath) throws IOException {
        // Placeholder for DOCX parsing using Apache POI
        // (Assuming XWPFDocument and XWPFWordExtractor are added to POM)
        return "Extracted text from DOCX: " + filePath;
    }
    
    public String parseDocument(String filePath) throws IOException {
        if (filePath.toLowerCase().endsWith(".pdf")) {
            return parsePdf(filePath);
        } else if (filePath.toLowerCase().endsWith(".docx")) {
            return parseDocx(filePath);
        }
        throw new IllegalArgumentException("Unsupported file type");
    }
}
