package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V17_CreateDeliveriesTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS deliveries (
                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

                order_id BIGINT UNSIGNED NOT NULL,

                -- Rider / courier info (optional for now)
                rider_name VARCHAR(100) NULL,

                delivery_address VARCHAR(255) NOT NULL,

                -- Delivery lifecycle
                status ENUM(
                    'PENDING',
                    'PREPARING',
                    'OUT_FOR_DELIVERY',
                    'DELIVERED',
                    'FAILED',
                    'CANCELLED'
                ) NOT NULL DEFAULT 'PENDING',

                estimated_time TIMESTAMP NULL,
                delivered_at TIMESTAMP NULL,

                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,

                -- Foreign Key
                CONSTRAINT fk_deliveries_order
                    FOREIGN KEY (order_id) REFERENCES orders(id)
                    ON DELETE CASCADE ON UPDATE CASCADE,

                -- Indexes
                INDEX idx_deliveries_order (order_id),
                INDEX idx_deliveries_status (status)
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
