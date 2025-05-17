package org.example.emailprojectjavafx.models;

import java.util.function.Consumer;

public class GenericPetition<T> {
    private String model;
    private String petition;
    private String method;
    private String data;
    private Class<T> responseClass;
    private Consumer<T> onSuccess;
    private String errorMessage;

    public GenericPetition(String model, String petition, String method, String data,
                           Class<T> responseClass, Consumer<T> onSuccess, String errorMessage) {
        this.model = model;
        this.method = method;
        this.data = data;
        this.responseClass = responseClass;
        this.onSuccess = onSuccess;
        this.errorMessage = errorMessage;
        this.petition = petition;
    }

    public String getModel() {
        return model;
    }

    public String getMethod() {
        return method;
    }

    public String getData() {
        return data;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public Consumer<T> getOnSuccess() {
        return onSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPetition() {
        return petition;
    }
}
