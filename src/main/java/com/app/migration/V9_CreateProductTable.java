package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V9_CreateProductTable implements Migration {
    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS products (
                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                category_id BIGINT UNSIGNED NOT NULL,
                size_id BIGINT UNSIGNED NOT NULL,
                price DECIMAL(10,2) NOT NULL,

                -- 1 = active (available), 0 = inactive (not for sale)
                availability TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1=active,0=inactive',

                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                -- Foreign Keys
                CONSTRAINT fk_products_category 
                    FOREIGN KEY (category_id) REFERENCES product_categories(id)
                    ON DELETE RESTRICT ON UPDATE CASCADE,

                CONSTRAINT fk_products_size 
                    FOREIGN KEY (size_id) REFERENCES product_sizes(id)
                    ON DELETE RESTRICT ON UPDATE CASCADE,

                -- Indexes
                INDEX idx_products_category (category_id),
                INDEX idx_products_size (size_id),
                INDEX idx_products_availability (availability),
                INDEX idx_products_name (name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
