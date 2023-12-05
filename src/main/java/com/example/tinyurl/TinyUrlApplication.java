package com.example.tinyurl;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
public class TinyUrlApplication {

    public static void main(String[] args) {
//        SpringApplication.run(TinyUrlApplication.class, args);
        new SpringApplicationBuilder(TinyUrlApplication.class).run(args);
    }
}
