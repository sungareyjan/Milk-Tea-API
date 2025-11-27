package com.app.repository;

import com.app.model.User;
import java.sql.SQLException;

public interface AuthRepository {
    User findByUsername(String username) throws SQLException;
}
