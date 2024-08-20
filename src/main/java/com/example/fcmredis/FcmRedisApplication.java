package com.example.fcmredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FcmRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcmRedisApplication.class, args);
    }

}
