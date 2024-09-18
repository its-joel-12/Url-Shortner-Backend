package com.shorturl.url_short_service.service;

import com.shorturl.url_short_service.auth.Authenticate;
import com.shorturl.url_short_service.exception.InvalidUrlException;
import com.shorturl.url_short_service.exception.UnauthorizedException;
import com.shorturl.url_short_service.model.UrlResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
//@ActiveProfiles("test")
@PropertySource("classpath:application.properties")
class UrlShortServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private Authenticate authenticate;

    @Autowired
    private UrlShortService urlShortService;

    @BeforeEach
    void setUp() {
    }

    // Test for shortUrlHash
    @Test
    void testShortUrlHash() {
        String expectedHash = "B!$!$!$";
        Long value = 1L;

        String result = urlShortService.shortUrlHash(value);

        assertEquals(expectedHash, result, "The generated hash should be correct");
    }

    // Test for isUrlValid with a valid URL
    @Test
    void testIsUrlValid_Success() throws IOException {
        URL mockUrl = mock(URL.class);
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);

        boolean isValid = urlShortService.isUrlValid("http://validurl.com");

        assertTrue(isValid, "The URL should be valid");
    }

    // Test for isUrlValid with an invalid URL
    @Test
    void testIsUrlValid_InvalidUrl() throws IOException {
        URL mockUrl = mock(URL.class);
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(404);

        boolean isValid = urlShortService.isUrlValid("http://invalidurl.com");

        assertFalse(isValid, "The URL should be invalid");
    }

    //     Test for saveUrl with valid URL and successful authentication
    @Test
    void testSaveUrl_Success() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        String longUrl = "https://stackoverflow.com/questions/2003505/how-do-i-delete-a-git-branch-locally-and-remotely";
        String token = "validToken";
        String shortUrl = "https://yourl.accelyazapcg.com/B!$!$!$";
        String expiryTime = "Tue Sep 03 15:14:30 IST 2024"; // 7 days in seconds

        // Mocked response for ValueOperations
//        ValueOperations<String, String> valueOps = mock(ValueOperations.class);

        UrlResponseDto expectedResponse = new UrlResponseDto(shortUrl, expiryTime);

        when(authenticate.decrypt(token)).thenReturn(true);

//        when(redisTemp.opsForValue()).thenReturn(valueOps);  // Mock opsForValue() method
//        when(valueOperations.get("HASH_COUNTER")).thenReturn("1");

//        when(redisTemplate.opsForValue().getExpire(shortUrlId)).thenReturn(expiryTime);

        // Call the service method
        UrlResponseDto result = urlShortService.saveUrl(longUrl, token);
//        System.out.println(");
        // Verify the result
        assertEquals(expectedResponse.getShortUrl(), result.getShortUrl(), "The shortened URL hashCode should be correct");
//        assertEquals(expectedResponse.getExpiry(), result.getExpiry(), "The expiry time should be correct");
        assertNotNull(result.getExpiry(),"The expiry time should not be null");
    }

    // Test for saveUrl with invalid URL
    @Test
    void testSaveUrl_InvalidUrl() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String longUrl = "http://invalidurl.com";
        String token = "validToken";
        when(authenticate.decrypt(token)).thenReturn(true);

        assertThrows(InvalidUrlException.class, () -> {
            urlShortService.saveUrl(longUrl, token);
        }, "Expected InvalidUrlException to be thrown for invalid URL");
    }

    // Test for saveUrl with unauthorized token
    @Test
    void testSaveUrl_Unauthorized() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String longUrl = "http://validurl.com";
        String token = "invalidToken";
        when(authenticate.decrypt(token)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            urlShortService.saveUrl(longUrl, token);
        }, "Expected UnauthorizedException to be thrown for unauthorized access");
    }

    // Test for getLongUrl when the hash code is valid
    @Test
    void testGetLongUrl_Success() {
        String hashCode = "B!$!$!$";
        String expectedLongUrl = "https://stackoverflow.com/questions/2003505/how-do-i-delete-a-git-branch-locally-and-remotely";
//        when(redisTemplate.opsForValue().get(hashCode)).thenReturn(expectedLongUrl);

        String longUrl = urlShortService.getLongUrl(hashCode);

        assertEquals(expectedLongUrl, longUrl, "The retrieved long URL should be correct");
    }

    // Test for getLongUrl when the hash code does not exist
    @Test
    void testGetLongUrl_NotFound() {
        String hashCode = "())(()";
//        when(redisTemplate.opsForValue().get(hashCode)).thenReturn(null);

        String longUrl = urlShortService.getLongUrl(hashCode);

        assertNull(longUrl, "The retrieved long URL should be null for non-existing hash");
    }

}
