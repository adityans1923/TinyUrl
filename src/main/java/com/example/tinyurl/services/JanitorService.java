package com.example.tinyurl.services;

import com.example.tinyurl.entity.URLStore;
import com.example.tinyurl.model.CounterFetchResult;
import com.example.tinyurl.repository.URLRepository;
import com.example.tinyurl.zookeeper.connection.ZooKeeperConnection;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.tinyurl.services.URLServices.CACHE_NAME;
import static com.example.tinyurl.util.LockingUtil.*;

@Component
public class JanitorService {
    private final static String LOCK_NAME = "write-event";
    private final static String LAST_CLEAN = "/lastclean";
    private final String BASE_LOCK = "/janitorlock";
    private final static Logger logger = LoggerFactory.getLogger(JanitorService.class);
    private final URLRepository urlRepository;
    private final CacheManager cacheManager;
    private final Long janitorTime;
    private Boolean init = false;
    private final static TimeUnit timeUnit = TimeUnit.SECONDS;

    public JanitorService(URLRepository urlRepository, CacheManager cacheManager, @Value("${server.janitor}") Long janitorTime) {
        this.urlRepository = urlRepository;
        this.cacheManager = cacheManager;
        this.janitorTime = janitorTime;
    }

    @EventListener
    private void handleRegistration(ApplicationStartedEvent event) throws InterruptedException {
        withLock(connection -> {
            logger.info("lock obtained successfully for {}", BASE_LOCK);
            Object obj = connection.existZk(BASE_LOCK);
            if (obj == null)
                connection.createZNode(BASE_LOCK, null);
            obj = connection.existZk(LAST_CLEAN);
            if (obj == null) {
                connection.createZNode(LAST_CLEAN, Long.toString(Instant.now().toEpochMilli()).getBytes());
            } else {
                connection.setData(LAST_CLEAN, Long.toString(Instant.now().toEpochMilli()).getBytes());
            }
            init = true;
            return Optional.empty();
        }, TINY_URL_PATH, ROOT_LOCK);
    }

    @Scheduled(fixedDelayString = "${server.janitor}", timeUnit = TimeUnit.SECONDS)
    public void clean() {
        if (init) {
            withLock(this::deleteRecord, BASE_LOCK, LOCK_NAME);
        }
    }

    private Optional<CounterFetchResult> deleteRecord(ZooKeeperConnection connection) throws InterruptedException, KeeperException{
        boolean shouldRun = false;
        byte[] bytes = connection.getData(LAST_CLEAN);
        if (Long.parseLong(new String(bytes)) + TimeUnit.SECONDS.toMillis(janitorTime) < Instant.now().toEpochMilli()) {
            connection.setData(LAST_CLEAN, Long.toString(Instant.now().toEpochMilli()).getBytes());
            shouldRun = true;
        }
        if (shouldRun) {
            List<URLStore> expired = urlRepository.findExpired(Instant.now());
            logger.info("janitor will clean " + expired.size() + " records");
            for (URLStore store: expired) {
                Objects.requireNonNull(this.cacheManager.getCache(CACHE_NAME)).evictIfPresent(store.getShortUrl());
            }
            //delete all entries
            try {
                urlRepository.deleteExpiredUrls(Instant.now());
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        return Optional.empty();
    }
}
