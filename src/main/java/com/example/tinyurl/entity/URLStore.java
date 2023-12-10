package com.example.tinyurl.entity;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Document(collection="shorturls")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class URLStore {

    public URLStore(String longUrl, String shortUrl,
                    Instant createdDate, Integer ttl,
                    String counter) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.createdDate  = createdDate;
        this.ttl = ttl;
        this.counter = counter;
        this.expiryDate = this.createdDate.plus(this.ttl, ChronoUnit.SECONDS);
    }

    String longUrl;

    @Indexed
    String shortUrl;

    Instant createdDate;
    Integer ttl;

    @Indexed
    Instant expiryDate;
    // we don't need to store counter but just for understanding directly we will
    String counter;
}
