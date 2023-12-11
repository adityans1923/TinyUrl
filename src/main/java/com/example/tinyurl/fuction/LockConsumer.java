package com.example.tinyurl.fuction;

import org.apache.zookeeper.KeeperException;

import java.util.Optional;

@FunctionalInterface
public interface LockConsumer<T,P> {
    Optional<P> accept(T t) throws InterruptedException, KeeperException;
}
