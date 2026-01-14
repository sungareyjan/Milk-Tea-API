package com.app.repository.impl;

import com.app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepositoryImpl {

    List<User> findAllUsers() throws SQLException;
    User findUserById(String publicId) throws SQLException;
    User insertUser(User user) throws SQLException;
    void updateUser(User user) throws SQLException;

}
