package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V18_CreateMerchantsTable implements Migration {
    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
        CREATE TABLE IF NOT EXISTS merchants (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            public_id CHAR(36) NOT NULL UNIQUE,
            name VARCHAR(150) NOT NULL,
            branch VARCHAR(150),
            address TEXT,
            contact_number VARCHAR(30),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        }
    }
}
