package com.example.fcmredis;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCommand;
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

    @Scheduled(fixedRate = 5000)
    public void sendMessageFromQueue() {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        System.out.println("MessageQueue: " + listOps.range("MessageQueue", 0, -1));

//        RedisClient redisClient = RedisClient.create("redis://:EUMru@2020@192.168.12.14:6379");
//        RedisCommands<String, String> commands = redisClient.connect().sync();

        // 무한 루프를 통해 작업을 처리
        while (true) {
            // 작업 큐에서 작업을 가져옴 (블로킹 방식)
            String task =  listOps.rightPop("MessageQueue");
            if (task != null) {
                // 작업 처리 로직 (여기서는 단순히 출력)
//                System.out.println("Processing: " + task);

                // FCM 발송 로직
                Message fcmMessage = Message.builder().putData("message", task).setTopic("general").build();

                try {
                    String response = FirebaseMessaging.getInstance().send(fcmMessage);
                    System.out.println("Successfully sent message: " + response);
                } catch (FirebaseMessagingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(10); // 5초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No tasks available, worker is idle...");
                try {
                    Thread.sleep(100); // 5초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

//        String message = listOps.rightPop("messageQueue");
//        if (message != null) {
//            sendFCMNotification(message);
//        }
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
