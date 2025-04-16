package org.example.emailprojectjavafx.models;

public class BaseResponse {
    private String error;

    public boolean isError() {
        return error != null;
    }

    public String getErrorMessage() {
        return error;
    }
}
