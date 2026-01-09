//package com.app.migration;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class V6_CreateCustomerTable implements Migration {
//    @Override
//    public void run(Connection connection) throws SQLException {
//        String query = """
//        CREATE TABLE IF NOT EXISTS customers (
//            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,   -- internal reference
//            public_id CHAR(50) NOT NULL UNIQUE,              -- UUID for external use
//
//            first_name VARCHAR(50) NOT NULL,
//            middle_name VARCHAR(50),
//            last_name VARCHAR(50) NOT NULL,
//
//            email VARCHAR(100) UNIQUE,
//            phone VARCHAR(20) UNIQUE,
//
//            street VARCHAR(255),
//            barangay VARCHAR(255),
//            city VARCHAR(100),
//            province VARCHAR(100),
//            postal_code VARCHAR(20),
//
//            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//            deleted_at TIMESTAMP NULL,                         -- soft delete (optional)
//
//            INDEX idx_customer_name (last_name, first_name),
//            INDEX idx_customer_email (email),
//            INDEX idx_customer_phone (phone)
//        ) ENGINE = InnoDB
//            DEFAULT CHARSET = utf8mb4
//            COLLATE = utf8mb4_unicode_ci;
//        """;
//
//        try (Statement statement = connection.createStatement()) {
//            statement.execute(query);
//        }
//    }
//
//}

package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V6_CreateCustomerTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
        CREATE TABLE IF NOT EXISTS customers (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            public_id CHAR(50) NOT NULL UNIQUE,

            first_name VARCHAR(50) NOT NULL,
            middle_name VARCHAR(50),
            last_name VARCHAR(50) NOT NULL,

            email VARCHAR(100) UNIQUE,
            phone VARCHAR(20) UNIQUE,

            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,
            deleted_at TIMESTAMP NULL,

            INDEX idx_customer_name (last_name, first_name),
            INDEX idx_customer_email (email),
            INDEX idx_customer_phone (phone)
        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
