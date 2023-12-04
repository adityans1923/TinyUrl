package com.example.tinyurl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@AllArgsConstructor
@Setter
@Getter
public class ShortUrlOutput {
    private String shortUrl;
    private Date createdDate;
    private Date expiryDate;
}
