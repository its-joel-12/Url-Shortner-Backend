package com.shorturl.url_short_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        UrlShortnerException error = new UrlShortnerException(
                404,
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "Required Resource Not Found, hence the operation was unsuccessful!!"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidUrlException.class)
    public ResponseEntity<Object> handleInvalidUrlException(InvalidUrlException ex) {
        UrlShortnerException error = new UrlShortnerException(
                400,
                HttpStatus.BAD_REQUEST,
                "Entered URL is invalid. Please enter a valid URL and try again.",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Unauthorised Exception
    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        UrlShortnerException error = new UrlShortnerException(
                401,
                HttpStatus.UNAUTHORIZED,
                "You don't have the access to this api",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        UrlShortnerException error = new UrlShortnerException(
                400,
                HttpStatus.BAD_REQUEST,
                "Please make sure you send data in proper format, also don't send an empty request body!!",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //NumberFormatException
    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex) {
        UrlShortnerException error = new UrlShortnerException(
                500,
                HttpStatus.BAD_REQUEST,
                "unable to fetch required resources for hashing the url",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // NoResourceFoundException
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        UrlShortnerException error = new UrlShortnerException(
                400,
                HttpStatus.BAD_REQUEST,
                "Check proper format of the api and its Http method!!",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // INVALID FIELD DATA
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> res = new HashMap<>();

        ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach((er) -> {
            String fieldName = ((FieldError) er).getField();
            String message = er.getDefaultMessage();
            res.put(fieldName, message);
        });
        UrlShortnerException error = new UrlShortnerException(
                400,
                HttpStatus.BAD_REQUEST,
                "Invalid Inputs !!",
                res.toString()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // UN IDENTIFIED EXCEPTION
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleUnidentifiedException(Exception ex) {
        UrlShortnerException error = new UrlShortnerException(
                500,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Un identified error | Please contact the Backend Developer!!... :D",
                ex.getMessage() + " | " + ex.getClass()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
