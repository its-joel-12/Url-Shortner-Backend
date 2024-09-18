package com.shorturl.url_short_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
//@PropertySource("classpath:application.properties")
public class RedisConfigTest {

    @Autowired
    private RedisConfig redisConfig;

    // Test case to verify the creation of RedisConnectionFactory
    @Test
    public void testRedisConnectionFactory() {
        RedisConnectionFactory factory = redisConfig.redisConnectionFactory();
        assertNotNull(factory, "RedisConnectionFactory should not be null");

        // Verify the Redis connection configurations
        JedisConnectionFactory jedisFactory = (JedisConnectionFactory) factory;
        assertNotNull(jedisFactory.getStandaloneConfiguration());
        assertNotNull(jedisFactory.getStandaloneConfiguration().getHostName());
        assertNotNull(jedisFactory.getStandaloneConfiguration().getPort());
        assertNotNull(jedisFactory.getStandaloneConfiguration().getUsername());
        assertNotNull(jedisFactory.getStandaloneConfiguration().getPassword());
        assertNotNull(jedisFactory.getStandaloneConfiguration().getDatabase());
    }

    // Test case to verify the creation of RedisTemplate
    @Test
    public void testRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = redisConfig.redisTemplate();

        // Assert that the RedisTemplate bean is not null
        assertNotNull(redisTemplate, "RedisTemplate should not be null");

        // Assert that the RedisTemplate has the correct connection factory
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        assertNotNull(connectionFactory, "RedisConnectionFactory should not be null");

        // Assert that the key serializer is set correctly
        assertEquals(StringRedisSerializer.class, redisTemplate.getKeySerializer().getClass(),
                "Key serializer should be of type StringRedisSerializer");

        // Assert that the value serializer is set correctly
        assertEquals(StringRedisSerializer.class, redisTemplate.getValueSerializer().getClass(),
                "Value serializer should be of type StringRedisSerializer");
    }
}
