package com.app.service;

import com.app.model.User;
import com.app.repository.AuthRepositoryImplementation;
import com.app.util.PasswordUtils;
import com.app.util.TokenUtils;

import java.sql.SQLException;

public class AuthService {

    private final AuthRepositoryImplementation userRepository;

    public AuthService(AuthRepositoryImplementation userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String username, String password) throws SQLException {
        User user = userRepository.findByUsername(username);
        if (user == null || !PasswordUtils.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return TokenUtils.generateToken(user);
    }

}
