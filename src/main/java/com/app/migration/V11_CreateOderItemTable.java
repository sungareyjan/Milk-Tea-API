package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V11_CreateOderItemTable implements Migration {
    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS order_items (
                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                order_id BIGINT UNSIGNED NOT NULL,

                -- Reference (for analytics)
                product_id BIGINT UNSIGNED NOT NULL,

                -- SNAPSHOT fields (receipt-safe)
                product_name VARCHAR(255) NOT NULL,
                    product_description TEXT,
                category_name VARCHAR(255),
                category_description TEXT,

                size VARCHAR(20),
                unit VARCHAR(20),          -- ml, g, oz
                measurement DECIMAL(10,2),

                quantity INT UNSIGNED NOT NULL DEFAULT 1,
                unit_price DECIMAL(10,2) NOT NULL,
                subtotal DECIMAL(10,2) NOT NULL,

                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                INDEX idx_order_items_order_id (order_id),
                INDEX idx_order_items_product_id (product_id),

                CONSTRAINT fk_order_items_order
                    FOREIGN KEY (order_id)
                    REFERENCES orders(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,

                CONSTRAINT fk_order_items_product
                    FOREIGN KEY (product_id)
                    REFERENCES products(id)
                    ON DELETE RESTRICT
                    ON UPDATE CASCADE
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try(Statement statement = connection.createStatement()){
            statement.execute(query);
        }
    }

}
