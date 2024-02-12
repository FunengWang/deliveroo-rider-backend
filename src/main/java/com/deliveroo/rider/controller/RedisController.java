package com.deliveroo.rider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/redis/{key}")
    public ResponseEntity<Object> get(@PathVariable("key") String key){
        Object value = redisTemplate.opsForValue().get(key);
        return ResponseEntity.ok().body(value);
    }

    @PostMapping("/redis")
    public ResponseEntity<Object> get(@RequestBody Map<String,Object> pair){
        if(pair==null || pair.isEmpty()){
            return ResponseEntity.badRequest().body("Request body should contain key and value pair!");
        }else {
            String key = (String) pair.get("key");
            Object value = pair.get("value");
            redisTemplate.opsForValue().set(key,value);
            return ResponseEntity.ok().body("Set key value succeed.");
        }
    }
}
