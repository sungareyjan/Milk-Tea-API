package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V16_CreateCustomerAddressTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
        CREATE TABLE IF NOT EXISTS customer_addresses (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            customer_id BIGINT UNSIGNED NOT NULL,

            street VARCHAR(255),
            barangay VARCHAR(255),
            city VARCHAR(100),
            province VARCHAR(100),
            postal_code VARCHAR(20),

            is_primary BOOLEAN DEFAULT TRUE,

            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

            CONSTRAINT fk_customer_address
                FOREIGN KEY (customer_id)
                REFERENCES customers(id)
                ON DELETE CASCADE,

            INDEX idx_address_customer (customer_id),
            INDEX idx_address_city (city),
            INDEX idx_address_province (province)
        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
