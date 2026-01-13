package com.global.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MemberFirmApp {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "initiator");
        SpringApplication.run(MemberFirmApp.class, args);
    }
}
