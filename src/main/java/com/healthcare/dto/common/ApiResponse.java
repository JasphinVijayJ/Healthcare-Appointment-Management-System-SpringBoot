package com.healthcare.dto.common;

public class ApiResponse {

    private String message;
    private String imageUrl;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String imageUrl, String message) {
        this.message = message;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
