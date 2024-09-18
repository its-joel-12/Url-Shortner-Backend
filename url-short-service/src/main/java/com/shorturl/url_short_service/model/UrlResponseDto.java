package com.shorturl.url_short_service.model;

public class UrlResponseDto {
    //    private final String domain = "http://localhost:8082/";
    private String shortUrl;
    private String expiry;

    public UrlResponseDto(String shortUrl, String expiry) {
        this.shortUrl = shortUrl;
        this.expiry = expiry;
    }

    public UrlResponseDto() {

    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
