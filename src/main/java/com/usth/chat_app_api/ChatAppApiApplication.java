package com.usth.chat_app_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class ChatAppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAppApiApplication.class, args);
    }

}
