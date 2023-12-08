package com.example.tinyurl.controller;

import com.example.tinyurl.model.LongUrlInput;
import com.example.tinyurl.model.ShortUrlOutput;
import com.example.tinyurl.services.URLServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.ExecutionException;

@RestController
public class UrlShortnerController {

    @Autowired
    private URLServices urlServices;

    @PostMapping("/generateShortUrl")
    public ShortUrlOutput generateShortUrl(@RequestBody LongUrlInput body) throws InterruptedException, ExecutionException {
        return urlServices.generateShortUrl(body);
    }

//    @GetMapping("/{shortUrl}")
//    public String redirectUrl(@PathVariable("shortUrl") String shortUrl) {
//        return urlServices.getOriginalURL(shortUrl);
//    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectUrl(@PathVariable("shortUrl") String shortUrl, HttpServletRequest request) {
        String buffer = request.getRequestURL().toString();
        String URL = buffer.split("(?<!/)/(?!/)")[0];
        // redireting to below url
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(URL + "/get/" + urlServices.getOriginalURL(shortUrl)))
                .build();
    }

    @GetMapping("/get/{longUrl}")
    public String getEcho(@PathVariable("longUrl") String longUrl) {
        return "data: " + longUrl;
    }
}
