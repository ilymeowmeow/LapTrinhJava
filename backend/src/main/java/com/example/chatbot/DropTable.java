package com.example.chatbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropTable {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/rag_db";
        String user = "postgres";
        String password = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS vector_store CASCADE;");
            System.out.println("SUCCESS: Dropped vector_store");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
