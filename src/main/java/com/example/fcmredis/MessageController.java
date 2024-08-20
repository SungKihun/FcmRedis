package com.example.fcmredis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public MessageController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestBody String message) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        System.out.println("message: " + message);
        listOps.leftPush("messageQueue", message);
        return "Message sent to Redis Queue.";
    }
}
