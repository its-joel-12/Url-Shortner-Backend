package com.shorturl.url_short_service.service;

import com.shorturl.url_short_service.auth.Authenticate;
import com.shorturl.url_short_service.exception.InvalidUrlException;
import com.shorturl.url_short_service.exception.UnauthorizedException;
import com.shorturl.url_short_service.model.UrlResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UrlShortService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Authenticate authenticate;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${shorturl.domain}")
    private String shortUrlDomain;

    //base62 string used for hashing
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    //length of the bse62 string
    private static final int BASE = BASE62.length();

    //method to generate hash value using base62 algorithm
    public String shortUrlHash(Long value) {

        StringBuilder result = new StringBuilder();
        while (value > 0) {
            result.append(BASE62.charAt((int) (value % BASE)));
            value /= BASE;
        }

        //padding to ensure the string is 7 characters long
        while (result.length() < 7) {
            if (result.length() % 2 == 0)
                result.append('$');
            else
                result.append('!');
        }
        return result.toString();
    }

    //method to check url validity
    public Boolean isUrlValid(String inputUrl) {
        try {
            URL url = new URL(inputUrl);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            int responseCode = huc.getResponseCode();

            if (responseCode == 404 || responseCode == 400) {
                huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                responseCode = huc.getResponseCode();
                if (responseCode != 404 && responseCode != 400) {
                    return true;
                }
                return false;
            } else {
                return true;
            }
        } catch (IOException ie) {
            return false;
        }
    }

    //saving short and long url mapping in redis db
    public UrlResponseDto saveUrl(String longUrl, String token) {
        //authenticate the source of the request
        try {
            if (!authenticate.decrypt(token)) {
                System.out.println("unauthorized..! access denied.");
                throw new UnauthorizedException("Unauthorized access. You are blocked from accessing these resources as you do not have necessary permissions");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized access. You are blocked from accessing these resources as you do not have necessary permissions");
        }

        //check long url validity
        if (isUrlValid(longUrl)) {
            Long counter;
            //create the counter for the first time
            redisTemplate.opsForValue().setIfAbsent("HASH_COUNTER", "1");
            //fetching the counter from the db
            String count = redisTemplate.opsForValue().get("HASH_COUNTER");

            //for testing purpose
            System.out.println("Profile: " + profile);
            if (Objects.equals(profile, "test")) {
                count = "1";
                System.out.println("Count: " + count);
            }

            //converting counter from string to long
            if (count != null) {
                counter = Long.valueOf(count);
            } else {
                throw new NumberFormatException("The server is unable to process a request due to an unexpected database issue");
            }

            //calling the function to generate hash value
            String shortUrlId = shortUrlHash(counter);
            //storing the hash value in the db along with long url
            redisTemplate.opsForValue().set(shortUrlId, longUrl);
            redisTemplate.expire(shortUrlId, 7, TimeUnit.DAYS);
            //incrementing the counter
            redisTemplate.opsForValue().increment("HASH_COUNTER");
            Long expiryInSecs = redisTemplate.getExpire(shortUrlId);
            // calculating the expiry date and time
            Date expiry = new Date(System.currentTimeMillis() + expiryInSecs * 1000);
            String expiryDateTime = expiry.toString();
            return new UrlResponseDto(shortUrlDomain + shortUrlId, expiryDateTime);
        } else {
            throw new InvalidUrlException(longUrl + " <-This Url doesn't exists !");
        }
    }

    //returning the long url for redirection
    public String getLongUrl(String hashCode) {
        System.out.println(hashCode);
        String longUrl = redisTemplate.opsForValue().get(hashCode);
        System.out.println(longUrl);
        return longUrl;
    }

}