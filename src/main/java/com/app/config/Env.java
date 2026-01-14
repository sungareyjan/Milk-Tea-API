package com.app.config;

import java.io.InputStream;
import java.util.Properties;

public class Env {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Env.class.getClassLoader().getResourceAsStream(".env")) {

            if (input != null) {
                properties.load(input);
            } else {
                System.err.println(".env not found in resources, using system environment variables");
            }

        } catch (Exception e) {
            System.err.println("Failed to load .env: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        // Check system environment
        String value = System.getenv(key);
        if (value != null) return value;

        // Check .env file
        return properties.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return get(key, null);
    }
}
