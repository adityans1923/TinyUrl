package com.example.tinyurl;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TinyUrlApplication {

    public static void main(String[] args) {
//        SpringApplication.run(TinyUrlApplication.class, args);
        new SpringApplicationBuilder(TinyUrlApplication.class).run(args);
    }
}
