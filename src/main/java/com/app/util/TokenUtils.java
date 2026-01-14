package com.app.util;

import com.app.config.Env;
import com.app.model.User;
import com.app.model.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

public class TokenUtils {

    private static final String SECRET =  Env.get("JWT_SECRET", "super_secret_key"); // move to env in production
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1 day

    public static String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("role", user.getRole().toString())
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .sign(algorithm);
    }

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // --- NEW METHOD ---
    public static User getUserFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        String username = jwt.getSubject();
        String roleStr = jwt.getClaim("role").asString();
        Role role = Role.valueOf(roleStr); // convert string back to enum

        return new User(null,username, null, role); // password can be null here
    }
}
