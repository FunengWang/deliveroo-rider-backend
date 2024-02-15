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
            /**
             * MONDAY/TUESDAY/WEDNESDAY/THURSDAY
             * 09:00 - 11:00 1.2x
             * 11:00 - 12:00 1.3x
             * 15:00 - 16:00 1.3x
             * 16:00 - 18:00 1.2x
             */
            /**MONDAY*/
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            /**TUESDAY*/
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.TUESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            /**WEDNESDAY*/
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            /**THURSDAY*/
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.THURSDAY, LocalTime.of(16, 0), LocalTime.of(18, 0), 1.2)));
            /**
             * FRIDAY
             * 08:00 - 11:00 1.2x
             * 11:00 - 12:00 1.2x
             * 15:00 - 18:00 1.2x
             * 18:00 - 21:00 1.5x
             */
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(11, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(18, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.FRIDAY, LocalTime.of(18, 0), LocalTime.of(21, 0), 1.5)));
            /**
             * SATURDAY
             * 9:00 - 10:00 1.2x
             * 11:00 - 12:00 1.2x
             * 15:00 - 16:00 1.3x
             * 16:00 - 19:00 1.2x
             *
             */
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(15, 0), LocalTime.of(16, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SATURDAY, LocalTime.of(16, 0), LocalTime.of(19, 0), 1.2)));
            /**
             * SUNDAY
             * 9:00 - 10:00 1,2x
             * 11:00 - 12:00 1.3x
             * 15:00 - 17:00 1.2x
             * 18:00 - 19:00 1.2x
             */
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), 1.3)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(15, 0), LocalTime.of(17, 0), 1.2)));
            set.add(JSON.toJSONString(new FeeBoost(DayOfWeek.SUNDAY, LocalTime.of(18, 0), LocalTime.of(19, 0), 1.2)));
        }
    }
}