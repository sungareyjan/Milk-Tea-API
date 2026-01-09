package com.app.repository.impl;

import com.app.model.ProductCategory;
import com.app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepositoryImpl {
    List<User> findAll() throws SQLException;
    User findById(String publicId) throws SQLException;
    User save(User user) throws SQLException;
    void update(User user) throws SQLException;
}
