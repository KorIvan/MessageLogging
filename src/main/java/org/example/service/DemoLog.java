package org.example.service;

import org.example.data.GroupHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoLog {
    public static void main(String[] args) {
        SpringApplication.run(DemoLog.class, args);
    }

    @Bean
    public GroupHandler groupHandler(){
        return new GroupHandler();
    }
}