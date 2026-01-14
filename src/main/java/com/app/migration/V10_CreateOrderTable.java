package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V10_CreateOrderTable implements Migration {
    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
        CREATE TABLE IF NOT EXISTS orders (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            public_id CHAR(50) NOT NULL UNIQUE,
        
            public_customer_id CHAR(50) NOT NULL,
            total_amount DECIMAL(10,2) NOT NULL,
            status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                COMMENT 'PENDING, PAID, COMPLETED, CANCELLED, REFUNDED',
        
            created_by VARCHAR(50),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        
            INDEX idx_orders_customer (public_customer_id),
            INDEX idx_orders_status (status),
            INDEX idx_orders_created_at (created_at),
        
            CONSTRAINT fk_orders_customer
                FOREIGN KEY (public_customer_id)
                REFERENCES customers(public_id)
                ON DELETE RESTRICT
                ON UPDATE CASCADE,
        
            CONSTRAINT fk_orders_created_by
                FOREIGN KEY (created_by)
                REFERENCES users(public_id)
                ON DELETE SET NULL
                ON UPDATE CASCADE
        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_unicode_ci;
        """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

}
