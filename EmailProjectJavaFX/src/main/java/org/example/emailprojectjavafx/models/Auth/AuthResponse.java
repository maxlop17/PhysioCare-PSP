package org.example.emailprojectjavafx.models.Auth;

public class AuthResponse {
    private boolean ok;
    private String result;

    public boolean isOk() {
        return ok;
    }

    public String getToken() {
        return result;
    }
}