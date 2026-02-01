package com.global.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableScheduling
public class UnifiedApp {
    private static final Logger log = LoggerFactory.getLogger(UnifiedApp.class);

    public static void main(String[] args) {
        String mode = System.getenv("APP_MODE");
        if (mode == null) {
            mode = "acceptor"; // default
        }

        log.info("Starting application in mode: {}", mode);
        System.setProperty("spring.profiles.active", mode);

        SpringApplication.run(UnifiedApp.class, args);
    }
}
