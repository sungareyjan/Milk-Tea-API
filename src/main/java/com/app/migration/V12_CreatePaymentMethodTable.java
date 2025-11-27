package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V12_CreatePaymentMethodTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
         CREATE TABLE IF NOT EXISTS payment_methods (
             id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
             name VARCHAR(50) NOT NULL UNIQUE,
             description VARCHAR(255),
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
         );
         """;
        try(Statement statement = connection.createStatement()){
            statement.execute(query);
        }
    }
}
