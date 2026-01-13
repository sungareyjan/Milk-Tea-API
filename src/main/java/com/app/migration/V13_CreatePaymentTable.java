package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V13_CreatePaymentTable implements Migration{

    @Override
    public void run(Connection connection) throws SQLException {
        String query= """
            CREATE TABLE IF NOT EXISTS payments (
                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                public_id CHAR(50) NOT NULL UNIQUE,
                public_order_id CHAR(50) NOT NULL,
            
                payment_method_name VARCHAR(50) NOT NULL,
                payment_method_description VARCHAR(255),
            
                amount_paid DECIMAL(10,2) NOT NULL,
            
                status VARCHAR(20) NOT NULL COMMENT 'PENDING, PAID, FAILED, VOID',
            
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            
                INDEX idx_payments_order_id (public_order_id),
                INDEX idx_payments_status (status),

            CONSTRAINT fk_payments_order FOREIGN KEY (public_order_id) REFERENCES orders(public_id) ON DELETE CASCADE ON UPDATE CASCADE
                ) ENGINE=InnoDB
                DEFAULT CHARSET=utf8mb4
                COLLATE=utf8mb4_unicode_ci;
            """;

        try(Statement statement = connection.createStatement()){
            statement.execute(query);
        }
    }
}
