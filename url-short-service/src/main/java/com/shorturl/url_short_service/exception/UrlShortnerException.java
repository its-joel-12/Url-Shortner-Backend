package com.shorturl.url_short_service.exception;

import org.springframework.http.HttpStatus;

public class UrlShortnerException{

    private final Integer httpCode;
    private final HttpStatus httpStatus;
    private final String message;
    private final String description;

    public UrlShortnerException(Integer httpCode, HttpStatus httpStatus, String message, String description) {
        this.httpCode = httpCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.description = description;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

}
