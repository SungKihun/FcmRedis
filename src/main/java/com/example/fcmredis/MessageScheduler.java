package com.example.fcmredis;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageScheduler {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public MessageScheduler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 100) // 5초마다 실행
    public void sendMessageFromQueue() {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        System.out.println("messageQueue: " + listOps.range("messageQueue", 0, -1));

        String message = listOps.rightPop("messageQueue");
        if (message != null) {
            sendFCMNotification(message);
        }
    }

    private void sendFCMNotification(String message) {
        // FCM 발송 로직
        Message fcmMessage = Message.builder().putData("message", message).setTopic("general").build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
