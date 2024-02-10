package com.deliveroo.rider.controller;

import com.deliveroo.rider.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/redis/{key}")
    public ResponseEntity<Object> get(@PathVariable("key") String key){
        Object value = redisService.getValue(key);
        return ResponseEntity.ok().body(value);
    }

    @PostMapping("/redis")
    public ResponseEntity<Object> get(@RequestBody Map<String,Object> pair){
        if(pair==null || pair.isEmpty()){
            return ResponseEntity.badRequest().body("Request body should contain key and value pair!");
        }else {
            String key = (String) pair.get("key");
            Object value = pair.get("value");
            redisService.setValue(key,value);
            return ResponseEntity.ok().body("Set key value succeed.");
        }
    }
}
