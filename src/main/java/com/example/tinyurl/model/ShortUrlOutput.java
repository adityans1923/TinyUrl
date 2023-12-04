package com.example.tinyurl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShortUrlOutput {
    private String shortUrl;
    private Instant createdDate;
    private Instant expiryDate;
}
