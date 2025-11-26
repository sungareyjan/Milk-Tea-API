package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V4_CreateUserRolesTable implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS user_roles (
            user_id BIGINT UNSIGNED NOT NULL,
            role_id INT UNSIGNED NOT NULL,
            
            PRIMARY KEY (user_id, role_id),
    
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
            CONSTRAINT fk_user_roles_user 
                FOREIGN KEY (user_id) REFERENCES users(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
    
            CONSTRAINT fk_user_roles_role
                FOREIGN KEY (role_id) REFERENCES roles(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
    
            INDEX idx_user_roles_user (user_id),
            INDEX idx_user_roles_role (role_id)
        ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
