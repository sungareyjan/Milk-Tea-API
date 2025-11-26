package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V10_CreateOrderTable implements Migration {
    @Override
    public void run(Connection connection) throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS orders (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            public_id CHAR(50) NOT NULL UNIQUE,
            customer_id BIGINT UNSIGNED NOT NULL,
            total_amount DECIMAL(10,2) NOT NULL,
            status TINYINT NOT NULL DEFAULT 0 COMMENT '0=pending,1=paid,2=completed,9=canceled',
            created_by BIGINT UNSIGNED,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

            INDEX idx_orders_customer (customer_id),
            INDEX idx_orders_status (status),
            INDEX idx_orders_created_at (created_at),

            CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
            CONSTRAINT fk_orders_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
        ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

}
