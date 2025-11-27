package com.app.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hash a raw password
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verify raw password against hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
