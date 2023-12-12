package com.example.tinyurl.util;

import com.example.tinyurl.fuction.LockConsumer;
import com.example.tinyurl.model.CounterFetchResult;
import com.example.tinyurl.zookeeper.DistributedLock;
import com.example.tinyurl.zookeeper.connection.ZooKeeperConnection;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LockingUtil {

    private static final Logger logger = LoggerFactory.getLogger(LockingUtil.class);
    public static final String ROOT_LOCK = "root-lock";
    public static final String TINY_URL_PATH = "/tinyurl";

    public static void withLock(LockConsumer<ZooKeeperConnection, CounterFetchResult> consumer, String basePath, String lockName) {
        // return statement is only used in case of counter fetch
        ZooKeeperConnection connection = null;
        try {
            connection = new ZooKeeperConnection();
            DistributedLock lock = null;
            try {
                lock = new DistributedLock(connection, basePath, lockName);
                lock.lock();
                consumer.accept(connection);
            } catch (KeeperException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (InterruptedException e) {
                    logger.error("exception while closing connection " + e.getMessage());
                }
            }
        }
    }
}
