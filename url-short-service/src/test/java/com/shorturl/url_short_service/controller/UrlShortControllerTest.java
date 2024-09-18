package com.shorturl.url_short_service.controller;

import com.shorturl.url_short_service.exception.UnauthorizedException;
import com.shorturl.url_short_service.model.UrlRequestDto;
import com.shorturl.url_short_service.model.UrlResponseDto;
import com.shorturl.url_short_service.service.UrlShortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UrlShortController.class)
public class UrlShortControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc is used to perform HTTP requests and verify responses

    @MockBean
    private UrlShortService urlShortService;  // Mock the UrlShortService to simulate its behavior

    String longUrl;
    //    String shortUrl;
    String shortUrl;
    String shortUrlId;
    String token;
    UrlResponseDto urlResponseDto;

    @BeforeEach
    void setUp() {
        longUrl = "https://example.com/jfjsnfs/sfjsnj/sdfjsnf/sdfjdbnf";
        shortUrlId = "abc123";
        shortUrl = "https://52.70.158.35:8080/abc123";
        token = "dummy_api_key";
        urlResponseDto = new UrlResponseDto(shortUrl, "Tue Sep 03 15:14:30 IST 2024"); // Assuming expiry of 7 days (in seconds)
    }

    // Testing the POST endpoint of the controller for success
    @Test
    void testShortenUrl_Success() throws Exception {
        UrlRequestDto urlRequestDto = new UrlRequestDto();
        urlRequestDto.setLongUrl(longUrl);

        // Mock the saveUrl method of UrlShortService to return UrlResponseDto
        when(urlShortService.saveUrl(anyString(), anyString())).thenReturn(urlResponseDto);

        // Perform a POST request to /url with a JSON body and check the response
        mockMvc.perform(post("/url")
                        .header("API_KEY", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"" + longUrl + "\"}"))
                .andExpect(status().isCreated())  // Expect HTTP status 201 Created
                .andExpect(jsonPath("$.shortUrl").value(shortUrl))  // Check the 'shortUrl' field
                .andExpect(jsonPath("$.expiry").value("Tue Sep 03 15:14:30 IST 2024"));  // Check the 'expiry' field
    }

    //testing the post endpoint of the controller for failure
    @Test
    void testShortenUrl_Failure() throws Exception {
        UrlRequestDto urlRequestDto = new UrlRequestDto();
        urlRequestDto.setLongUrl(longUrl);

        // Simulate a failure in the service, e.g., unauthorized access
        when(urlShortService.saveUrl(anyString(), anyString())).thenThrow(new UnauthorizedException("Unauthorized access"));

        // Perform a POST request to /url with a JSON body and expect failure
        mockMvc.perform(post("/url")
                        .header("API_KEY", "dummy_api_key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longUrl\":\"" + longUrl + "\"}"))
                .andExpect(status().isUnauthorized())  // Expect HTTP status 401 Unauthorized
                .andExpect(jsonPath("$.httpCode").value(401))  // Check the 'httpCode' field
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))  // Check the 'httpStatus' field
                .andExpect(jsonPath("$.message").value("You don't have the access to this api"))  // Check the 'message' field
                .andExpect(jsonPath("$.description").value("Unauthorized access"));  // Check the 'description' field
    }

    //testing the get endpoint of the controller for success
    @Test
    void testRedirectToLongUrl() throws Exception {
        // Mock the getLongUrl method of UrlShortService to return the long URL
        when(urlShortService.getLongUrl(shortUrlId)).thenReturn(longUrl);
        // Perform a GET request to /{shortUrlId} and check the response
        mockMvc.perform(get("/" + shortUrlId))
                .andExpect(status().isFound())  // Expect HTTP status 302 Found
                .andExpect(header().string("Location", longUrl));  // Check if the Location header contains the long URL
    }

    //testing the get endpoint of the controller for failure
    @Test
    void testRedirectToLongUrl_Failure() throws Exception {

        // Mock the getLongUrl method of UrlShortService to return null (indicating that the short URL is not found)
        when(urlShortService.getLongUrl(shortUrlId)).thenReturn(null);

        mockMvc.perform(get("/" + shortUrlId))
                .andExpect(status().isNotFound())  // Expect HTTP status 302 OK
                .andExpect(content().contentType(MediaType.TEXT_HTML))  // Expect content type to be text/html
                .andExpect(content().string(containsString("404")));  // Check if the response contains the content from ErrorPage.html
    }

}
