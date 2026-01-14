package com.app.controller;

import com.app.model.User;
import com.app.service.UserService;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void getAllUsers(Context context) throws SQLException{
        context.json(userService.getAllUsers());
    }
    public void getUserById(Context context) throws SQLException {
        String publicId = context.pathParam("public_id"); // get path param
        User user = userService.getUserByPublicId(publicId);
        if (user != null) {
            context.json(user); // return the user as JSON
        } else {
            context.status(404).result("User not found"); // simple 404
        }
    }

    public void createUser(Context context) throws SQLException{
        User user = context.bodyAsClass(User.class);
        userService.createUser(user);
        context.status(201).json(user);
    }

    public void updateUser(Context context) throws SQLException{
        String publicId = context.pathParam("public_id");
        User user = context.bodyAsClass(User.class);
        user.setPublicId(publicId);
        userService.updateUser(user);
        context.json(user);
    }
}
