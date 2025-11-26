package com.app.database;

import com.app.config.Env;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                final String HOST = Env.get("DB_HOST", "localhost");
                final String PORT = Env.get("DB_PORT", "3306");
                final String DATA_BASE_NAME = Env.get("DB_NAME", "milktea_db");
                final String USERNAME = Env.get("DB_USER", "root");
                final String PASSWORD = Env.get("DB_PASS", "");

                String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", HOST, PORT, DATA_BASE_NAME);
                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
                System.out.println("Database Connection Success");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to DB: " + e.getMessage(), e);
        }
        return connection;
    }
}
