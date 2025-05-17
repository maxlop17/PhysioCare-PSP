package org.example.emailprojectjavafx.utils.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.emailprojectjavafx.models.User.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static org.example.emailprojectjavafx.utils.services.ServiceUtils.SERVER;

public class TokenUtils {
    public static String token = null;
    private static DecodedJWT decodedJWT;
    public static final Path tokenPath = Paths.get(System.getProperty("user.dir") +
            "/EmailProjectJavaFX/tokens/token.txt");

    public static void decodeToken() {
        decodedJWT =  JWT.decode(token);
    }


    public static void setToken(String token) {
        TokenUtils.token = token;
    }

    /**
     * Method that removes the token from the saved file and the local static variable
     */
    public static void removeToken() {
        TokenUtils.token = null;
        try{
            Files.writeString(tokenPath, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error removing token: " + e.getMessage());
        }
    }

    /**
     * Method to get the user from the token
     * @return
     */
    public static User getUserFromToken(){
        return new User(decodedJWT.getId(), decodedJWT.getClaim("login").asString(),
                decodedJWT.getClaim("rol").asString(), decodedJWT.getClaim("avatar").asString());
    }

    /**
     * Method to save the token in the system
     * @param token
     */
    public static void saveToken(String token, Path tokenPath){
        try {
            Files.writeString(tokenPath, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e){
            System.out.println("Error saving the token: " + e.getMessage());

        }
    }

    /**
     * Method that gets the saved token
     * @return
     */
    public static void getToken() {
        try {
            if (Files.exists(tokenPath)) {
                token = Files.readString(tokenPath);
            }
        } catch (IOException e){
            System.out.println("Error getting token: " + e.getMessage());
        }
    }

    /**
     * Method that makes a petition to the server and checks if the token is valid
     * @return
     */
    public static boolean validToken() {
        try{
            String response = ServiceUtils.getResponse(SERVER + "/patients", null, "GET");
            System.out.println(response);
            return !Arrays.asList(response.split("\"")).get(5).contains("Token");
        } catch (Exception e) {
            return false;
        }
    }
}
