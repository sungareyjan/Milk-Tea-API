package com.app.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Verify raw password against hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    // Hash password before saving
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }
}
