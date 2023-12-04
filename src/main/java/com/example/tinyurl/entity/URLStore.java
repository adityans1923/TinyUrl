package com.example.tinyurl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection="shorturls")
@AllArgsConstructor
@ToString
@Getter
@Setter
public class URLStore {

    String longUrl;

    @Indexed
    String shortUrl;

    @Indexed
    Instant createdDate;
    Integer ttl;

    // we don't need to store counter but just for understanding directly we will
    String counter;
}
