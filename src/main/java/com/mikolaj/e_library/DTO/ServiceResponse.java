package com.mikolaj.e_library.DTO;

import java.util.Optional;

public class ServiceResponse<T> {
    private Optional<T> data;
    private String message;


    public ServiceResponse(Optional<T> data, String message) {
        this.data = data;
        this.message = message;
    }

    public Optional<T> getData() {
        return data;
    }

    public void setData(Optional<T> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
