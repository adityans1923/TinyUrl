package com.example.tinyurl.repository;


import com.example.tinyurl.entity.URLStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface URLRepository extends MongoRepository<URLStore, String> {
    @Query("{shortUrl: '?0'}")
    URLStore findByShortUrl(String shortUrl);

    List<URLStore> findByLongUrl(String longUrl);

    List<URLStore> findByCreatedDate(Instant createdDate);
}
