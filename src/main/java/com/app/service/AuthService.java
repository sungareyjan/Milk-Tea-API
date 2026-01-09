package com.app.service;

import com.app.model.User;
import com.app.repository.AuthRepository;
import com.app.service.impl.AuthServiceImpl;
import com.app.util.PasswordUtils;
import com.app.util.TokenUtils;

import java.sql.SQLException;

public class AuthService implements AuthServiceImpl {

    private final AuthRepository userRepository;

    public AuthService(AuthRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String login(String username, String password) throws SQLException {
        User user = userRepository.findByUsername(username);
        if (user == null || !PasswordUtils.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return TokenUtils.generateToken(user);
    }

}
