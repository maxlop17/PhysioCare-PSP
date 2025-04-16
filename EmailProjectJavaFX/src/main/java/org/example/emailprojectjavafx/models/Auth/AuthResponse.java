package org.example.emailprojectjavafx.models.Auth;

import org.example.emailprojectjavafx.models.BaseResponse;

public class AuthResponse extends BaseResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "ok= " + isOk() +
                ", token='" + token + '\'' +
                '}';
    }
}