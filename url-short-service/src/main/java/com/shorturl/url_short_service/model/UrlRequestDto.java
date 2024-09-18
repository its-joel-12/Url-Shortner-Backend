package com.shorturl.url_short_service.model;

public class UrlRequestDto {
    private String longUrl;

    public UrlRequestDto(String longUrl) {
        this.longUrl = longUrl;
    }

    public UrlRequestDto() {

    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    @Override
    public String toString() {
        return "UrlModel{" + "longUrl='" + longUrl + '\'' + '}';
    }
}
