package com.example.tinyurl.services;


import com.example.tinyurl.entity.URLStore;
import com.example.tinyurl.model.LongUrlInput;
import com.example.tinyurl.model.ShortUrlOutput;
import com.example.tinyurl.repository.URLRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

@Component
public class URLServices {

    private final URLRepository urlRepository;
    private final CounterService counterService;
    private final BaseEncoder encoder;

    URLServices(final URLRepository urlRepository,
                        final CounterService counterService,
                        final BaseEncoder encoder) {
        this.urlRepository = urlRepository;
        this.counterService = counterService;
        this.encoder = encoder;
    }

    public ShortUrlOutput generateShortUrl(LongUrlInput longUrlInput){
        try {
            BigInteger counter = counterService.getCurrentAndIncrement();
            String shortUrl = encoder.encode(counter);
            Instant createdDate = Calendar.getInstance().toInstant();
            int ttl = 5;
            URLStore urlentry = new URLStore(longUrlInput.getLongUrl(), shortUrl, createdDate, ttl, counter.toString());
            urlRepository.save(urlentry);
            return new ShortUrlOutput(shortUrl, createdDate, createdDate.plusSeconds(ttl));
        } catch (ExecutionException| InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = "longUrl", key = "#shortUrl")
    public String getOriginalURL(String shortUrl) {
        URLStore longUrl = urlRepository.findByShortUrl(shortUrl);
        if (longUrl == null) {
            return "";
        }
        System.out.println(longUrl);
        return longUrl.getLongUrl();
    }
}
