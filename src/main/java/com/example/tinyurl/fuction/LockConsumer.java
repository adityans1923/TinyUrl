package com.example.tinyurl.fuction;

import org.apache.zookeeper.KeeperException;

@FunctionalInterface
public interface LockConsumer<T,P> {
    void accept(T t) throws InterruptedException, KeeperException;
}
