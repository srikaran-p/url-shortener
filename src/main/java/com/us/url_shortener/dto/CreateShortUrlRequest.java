package com.us.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateShortUrlRequest {

    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
