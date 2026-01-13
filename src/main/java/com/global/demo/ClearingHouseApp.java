package com.global.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClearingHouseApp {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "acceptor");
        SpringApplication.run(ClearingHouseApp.class, args);
    }
}
