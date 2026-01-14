package com.app.service;

import com.app.model.User;
import com.app.repository.UserRepository;
import com.app.service.impl.UserServiceImpl;
import com.app.util.PasswordUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserService implements UserServiceImpl {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return repository.findAllUsers();
    }

    @Override
    public User getUserByPublicId(String publicId) throws SQLException {
        return repository.findUserById(publicId);
    }

    @Override
    public void createUser(User user) throws SQLException {
        user.setPublicId(UUID.randomUUID().toString());
        // Hash here
        user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        repository.insertUser(user);
    }

    @Override
    public void updateUser(User user) throws SQLException {
        // Hash here
        user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        repository.updateUser(user);
    }


}
