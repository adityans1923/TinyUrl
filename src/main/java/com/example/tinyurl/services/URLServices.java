package com.example.tinyurl.services;


import com.example.tinyurl.entity.URLStore;
import com.example.tinyurl.model.LongUrlInput;
import com.example.tinyurl.model.ShortUrlOutput;
import com.example.tinyurl.repository.URLRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class URLServices {

    private static final String CACHE_NAME = "URLS";
    private static final Integer URL_TTL = 5;

    private final URLRepository urlRepository;
    private final CounterService counterService;
    private final BaseEncoder encoder;
    private final CacheManager cacheManager;

    URLServices(final URLRepository urlRepository,
                        final CounterService counterService,
                        final CacheManager cacheManager,
                        final BaseEncoder encoder) {
        this.urlRepository = urlRepository;
        this.counterService = counterService;
        this.encoder = encoder;
        this.cacheManager = cacheManager;
    }

    public ShortUrlOutput generateShortUrl(LongUrlInput longUrlInput){
        try {
            BigInteger counter = counterService.getCurrentAndIncrement();
            String shortUrl = encoder.encode(counter);
            Instant createdDate = Calendar.getInstance().toInstant();
            URLStore urlentry = new URLStore(longUrlInput.getLongUrl(), shortUrl, createdDate, URL_TTL, counter.toString());
            urlRepository.save(urlentry);
            return new ShortUrlOutput(shortUrl, createdDate, urlentry.getExpiryDate());
        } catch (ExecutionException| InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = CACHE_NAME, key = "#shortUrl")
    public String getOriginalURL(String shortUrl) {
        URLStore longUrl = urlRepository.findByShortUrl(shortUrl);
        if (longUrl == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } else if (longUrl.getExpiryDate().compareTo(Instant.now()) <= 0) {
            Objects.requireNonNull(this.cacheManager.getCache(CACHE_NAME)).evictIfPresent(shortUrl);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TTL is expired");
        }
        System.out.println(longUrl);
        return longUrl.getLongUrl();
    }

    @Scheduled(fixedDelay = 30, timeUnit=TimeUnit.SECONDS)
    protected void janitor() {
        List<URLStore> expired = urlRepository.findExpired(Instant.now());
        for (URLStore store: expired) {
            Objects.requireNonNull(this.cacheManager.getCache(CACHE_NAME)).evictIfPresent(store.getShortUrl());
        }
        //delete all entries
        try {
            urlRepository.deleteExpiredUrls(Instant.now());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
