package com.example.tinyurl.services;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class BaseEncoder {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Map<Character, Integer> mp = IntStream.range(0, BASE62.length()).boxed()
            .collect(Collectors.toMap(BASE62::charAt, i->i));

    private static final BigInteger BASE = BigInteger.valueOf(62);

    public String encode(BigInteger num) {
        StringBuilder stringBuilder = new StringBuilder(80);
        do {
            BigInteger rem = num.mod(BASE);
            stringBuilder.insert(0, BASE62.charAt(rem.intValue()));
            num = num.divide(BASE);
        } while (!num.equals(BigInteger.ZERO));
        return stringBuilder.toString();
    }

    public BigInteger decode(String str) {
        BigInteger num = BigInteger.ZERO;
        for (int i=str.length() - 1; i >= 0; i--) {
            int index = mp.get(str.charAt(i));
            num = num.multiply(BASE).add(BigInteger.valueOf(index));
        }
        return num;
    }
}
