package com.example.tinyurl.model;

import com.example.tinyurl.enums.CounterFetchStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterFetchResult {
    private CounterFetchStatus status;
    private BigInteger newRange;
}
