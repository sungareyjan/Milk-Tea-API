package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V5_CreateRolePermissionsTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
        CREATE TABLE IF NOT EXISTS role_permissions (
            role_id INT UNSIGNED NOT NULL,
            permission_id BIGINT UNSIGNED NOT NULL,
    
            PRIMARY KEY (role_id, permission_id),
    
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
            CONSTRAINT fk_role_permissions_role
                FOREIGN KEY (role_id) REFERENCES roles(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
    
            CONSTRAINT fk_role_permissions_permission
                FOREIGN KEY (permission_id) REFERENCES permissions(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
    
            INDEX idx_role_permissions_role (role_id),
            INDEX idx_role_permissions_permission (permission_id)
        ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
