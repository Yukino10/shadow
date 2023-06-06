package com.cislc.shadow.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ShadowQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShadowQueueApplication.class, args);
    }

}
