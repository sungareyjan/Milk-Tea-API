package com.app.service.impl;

import java.sql.SQLException;

public interface AuthServiceImpl {
    String login(String username, String password) throws SQLException;
}
