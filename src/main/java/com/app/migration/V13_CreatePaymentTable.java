package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V13_CreatePaymentTable implements Migration{

    @Override
    public void run(Connection connection) throws SQLException {
        String sql= """
        CREATE TABLE IF NOT EXISTS payments (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            public_id CHAR(50) NOT NULL UNIQUE,
            order_id BIGINT UNSIGNED NOT NULL,
            payment_method_id TINYINT UNSIGNED NOT NULL,
            amount_paid DECIMAL(10,2) NOT NULL,
            status TINYINT NOT NULL DEFAULT 0 COMMENT '0=pending,1=success,2=failed,3=void',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        
            INDEX idx_payments_order_id (order_id),
            INDEX idx_payments_method_id (payment_method_id),
            INDEX idx_payments_status (status),
        
            CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
            CONSTRAINT fk_payments_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id) ON DELETE RESTRICT ON UPDATE CASCADE
        ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try(Statement statement = connection.createStatement()){
            statement.execute(sql);
        }
    }
}
