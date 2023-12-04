package com.example.tinyurl.controller;

import com.example.tinyurl.model.LongUrlInput;
import com.example.tinyurl.model.ShortUrlOutput;
import com.example.tinyurl.services.URLServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class UrlShortnerController {

    @Autowired
    private URLServices urlServices;

    @PostMapping("/generateShortUrl")
    public ShortUrlOutput generateShortUrl(@RequestBody LongUrlInput body) throws InterruptedException, ExecutionException {
        return urlServices.generateShortUrl(body);
    }

    @GetMapping("/{shortUrl}")
    public String redirectUrl(@PathVariable("shortUrl") String shortUrl) {
        return urlServices.getOriginalURL(shortUrl);
    }
}
