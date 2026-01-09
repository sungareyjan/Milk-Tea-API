package com.app.repository.impl;

import com.app.model.User;
import java.sql.SQLException;

public interface AuthRepositoryImpl {
    User findByUsername(String username) throws SQLException;
}
