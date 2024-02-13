package com.deliveroo.rider.configuration;

import com.alibaba.fastjson.JSON;
import com.deliveroo.rider.entity.FeeBoost;
import com.deliveroo.rider.pojo.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Objects;

import static com.deliveroo.rider.util.Constants.*;

@Component
public class RedisInitializer {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


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
        if (Boolean.FALSE.equals(connection.exists(FEE_BOOSTS_KEY.getBytes()))) {
            BoundSetOperations<String, Object> set = redisTemplate.boundSetOps(FEE_BOOSTS_KEY);
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.TUESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.THURSDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));

            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(18, 0), LocalTime.of(21, 0), 1.5)));

            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(14, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));

            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(15, 0), LocalTime.of(17, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(17, 0), LocalTime.of(19, 0), 1.2)));
        }
    }
}