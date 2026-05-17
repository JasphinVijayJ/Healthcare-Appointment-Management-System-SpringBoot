package com.healthcare.dto.common;

public class ApiResponse {

    private String message;
    private String data;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String data, String message) {
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
