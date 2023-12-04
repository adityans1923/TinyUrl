package com.example.tinyurl.controller;

import com.example.tinyurl.model.LongUrlInput;
import com.example.tinyurl.model.ShortUrlOutput;
import com.example.tinyurl.services.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
public class UrlShortnerController {

    @Autowired
    private CounterService counterService;

    @PostMapping("/generateShortUrl")
    public ShortUrlOutput generateShortUrl(@RequestBody LongUrlInput body) throws InterruptedException, ExecutionException {
        ShortUrlOutput output = new ShortUrlOutput("http://localhost:11000/abcd",
                new Date(),
                new Date()
                );
        BigInteger currentValue = counterService.getCurrentAndIncrement();
        output.setShortUrl("http://localhost:1100/" + currentValue.intValue());
        return output;
    }

    @GetMapping("/{shortUrl}")
    public String redirectUrl(@PathVariable("shortUrl") String shortUrl) {
        return "haha";
    }
}
