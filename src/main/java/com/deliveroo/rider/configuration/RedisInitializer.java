package com.deliveroo.rider.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class RedisInitializer {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String[] PLACES = {"GAL", "SHT"};
    private static final String PLACES_KEY = "places";
    private static final String[] SHOPS = {"Supermac's", "McDonald's", "Subway", "Burger King", "Papa John's"};
    private static final String SHOPS_KEY = "shops";

    @PostConstruct
    public void init() {
        // Add your custom key-value pairs to Redis
        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        if (Boolean.FALSE.equals(connection.exists(PLACES_KEY.getBytes()))) {
            for (String place : PLACES) {
                redisTemplate.opsForList().leftPush(PLACES_KEY, place);
            }
        }
        if (Boolean.FALSE.equals(connection.exists(SHOPS_KEY.getBytes()))) {
            for (String shop : SHOPS) {
                redisTemplate.opsForList().leftPush(SHOPS_KEY, shop);
            }
        }
    }
}