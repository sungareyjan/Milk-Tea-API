package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V8_CreateProductSizeTable implements Migration{
    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS product_sizes (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(50) NOT NULL UNIQUE,  -- e.g., Small, Medium, Large
            description VARCHAR(255),
            deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1=true,0=false',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """;

        try(Statement statement = connection.createStatement()){
            statement.execute(query);
        }
    }

}
