package com.app.service.impl;

import com.app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserServiceImpl {

    List<User> getAllUsers() throws SQLException;
    User getUserByPublicId(String publicId) throws SQLException;
    void createUser(User user) throws SQLException;
    void updateUser(User user) throws  SQLException;

}
