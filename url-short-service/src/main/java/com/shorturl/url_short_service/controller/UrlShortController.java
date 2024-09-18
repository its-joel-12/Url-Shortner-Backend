package com.shorturl.url_short_service.controller;

import com.shorturl.url_short_service.model.UrlRequestDto;
import com.shorturl.url_short_service.model.UrlResponseDto;
import com.shorturl.url_short_service.service.UrlShortService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
//@CrossOrigin
public class UrlShortController {

    @Autowired
    private UrlShortService urlShortService;

    @Autowired
    private ResourceLoader resourceLoader;

    //post request to input long url and return short url
    @PostMapping("url")
    public ResponseEntity<UrlResponseDto> ShortenUrl(
            @RequestHeader(value = "API_KEY") String token,
            @Valid @RequestBody UrlRequestDto urlModel) {
//        System.out.println("inside the post controller");
        return new ResponseEntity<>(urlShortService.saveUrl(urlModel.getLongUrl(), token), HttpStatus.CREATED);
    }

    //    get request to redirect to the long url
    @GetMapping("/{shortUrlId}")
    public ResponseEntity<Resource> redirectToLongUrl(@PathVariable String shortUrlId) {
        String longUrl = urlShortService.getLongUrl(shortUrlId);
        System.out.println(longUrl);
        if (longUrl == null) {
            Resource resource = resourceLoader.getResource("classpath:/static/ErrorPage.html");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_HTML).body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
        }
    }

}
