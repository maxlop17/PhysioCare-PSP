package org.example.emailprojectjavafx.utils.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.emailprojectjavafx.models.User;

public class TokenUtils {
    private static DecodedJWT decodedJWT;

    public static void decodeToken(String token) {
        decodedJWT =  JWT.decode(token);
    }

    public static User getUserFromToken(){
        return new User(decodedJWT.getId(), decodedJWT.getClaim("login").asString(),
                decodedJWT.getClaim("rol").asString());
    }
}
