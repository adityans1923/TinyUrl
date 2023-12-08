package com.example.tinyurl.services;

import com.example.tinyurl.enums.CounterFetchStatus;
import com.example.tinyurl.model.CounterFetchResult;
import com.example.tinyurl.zookeeper.DistributedLock;
import com.example.tinyurl.zookeeper.connection.ZooKeeperConnection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.*;


@Component
public class CounterService  {

    // static internal constants
    private final static String LOCK_NAME = "write-event";
    private final static String RANGE_NODE = "/range";
    private final static int DIFF_LIMIT = 1;
    private final static int RETRY = 5;
    private final static int SLEEP_TIME = 2000;


    // internal constants filled at runtime
    private final String BASE_LOCK = "/counterlock";
    private final BigInteger RANGE_SIZE = BigInteger.valueOf(5);

    // variables
    private BigInteger startCount;
    private BigInteger endCount;
    private volatile BigInteger currentCount;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<CounterFetchResult> threadFuture = null;
    private CountDownLatch initialized = new CountDownLatch(1);

    @Autowired
    private ApplicationContext appContext;

    @EventListener
    private void handleRegistration(ApplicationStartedEvent event) throws InterruptedException {
        for (int i=0;i < RETRY ; i++) {
            CounterFetchResult result = requestingFreshCounter(true);
            if (result.getStatus() == CounterFetchStatus.Completed && result.getNewRange() != null) {
                setStartCount(result.getNewRange());
                initialized.countDown();
                return;
            } else {
                Thread.sleep(SLEEP_TIME);
            }
        }
        SpringApplication.exit(appContext, () -> 500);
    }

    private void setStartCount(BigInteger current) {
        this.startCount = current;
        this.endCount = this.startCount.add(RANGE_SIZE);
        this.currentCount = this.startCount;
        threadFuture = null;
    }

    public synchronized BigInteger getCurrentAndIncrement() throws InterruptedException, ExecutionException {
        initialized.await(5, TimeUnit.SECONDS);
        int compare = endCount.subtract(currentCount).compareTo(BigInteger.valueOf(DIFF_LIMIT));
        if (compare <= 0) {
            if (threadFuture == null) {
                threadFuture = executor.submit(() -> {
                    for (int i=0;i<RETRY;i++) {
                        CounterFetchResult result = requestingFreshCounter(false);
                        if (result.getStatus() == CounterFetchStatus.Completed && result.getNewRange() != null) {
                            return result;
                        } else {
                            Thread.sleep(SLEEP_TIME);
                        }
                    }
                    return new CounterFetchResult(CounterFetchStatus.Failed, null);
                });
            }
            if (compare < 0 && endCount.equals(currentCount)) {
                CounterFetchResult result = threadFuture.get();
                if (result.getStatus() == CounterFetchStatus.Completed && result.getNewRange() != null) {
                    setStartCount(result.getNewRange());
                } else {
                    SpringApplication.exit(appContext, () -> 500);
                }
            }
        }
        BigInteger cur = currentCount;
        this.currentCount = currentCount.add(BigInteger.ONE);
        return cur;
    }

    private synchronized CounterFetchResult requestingFreshCounter(boolean isInit) {
        ZooKeeperConnection connection = null;
        try {
            connection = new ZooKeeperConnection();
            DistributedLock lock = null;
            try {
                lock = new DistributedLock(connection, BASE_LOCK, LOCK_NAME);
                lock.lock();

                BigInteger newCounter = null;
                if (isInit) {
                    Stat stat = connection.existZk(RANGE_NODE);
                    if (stat == null) {
                        connection.createZNode(RANGE_NODE, RANGE_SIZE.toByteArray());
                        newCounter = BigInteger.ZERO;
                    } else {
                        newCounter = updateNewRange(connection);
                    }
                } else {
                    newCounter = updateNewRange(connection);
                }
                return new CounterFetchResult(CounterFetchStatus.Completed, newCounter);
            } catch (KeeperException e) {
                throw new IOException(e);
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (InterruptedException e) {
                    System.out.println("exception while closing connection " + e.getMessage());
                }
            }
        }
        return new CounterFetchResult(CounterFetchStatus.Failed, null);
    }
    private BigInteger updateNewRange(ZooKeeperConnection connection) throws InterruptedException, KeeperException {
        byte[] data = connection.getData(RANGE_NODE);
        BigInteger current = new BigInteger(data);
        BigInteger newVal = current.add(RANGE_SIZE);
        connection.setData(RANGE_NODE, newVal.toByteArray());
        return current;
    }

}
