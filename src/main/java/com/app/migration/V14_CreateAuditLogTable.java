package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V14_CreateAuditLogTable implements Migration{

    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
        CREATE TABLE IF NOT EXISTS audit_logs (
            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
            entity_name VARCHAR(100) NOT NULL,           -- Table or entity being changed, e.g., users, orders
            entity_id BIGINT UNSIGNED NOT NULL,          -- ID of the entity being changed
            action VARCHAR(50) NOT NULL,                 -- create, update, delete, login, etc.
            old_data JSON NULL,                          -- previous data before change (optional)
            new_data JSON NULL,                          -- new data after change (optional)
            performed_by BIGINT UNSIGNED,                -- user_id who performed the action
            ip_address VARCHAR(50),                      -- optional, for security/audit purposes
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
            INDEX idx_audit_entity (entity_name, entity_id),
            INDEX idx_audit_user (performed_by)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        """;

        try(Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
