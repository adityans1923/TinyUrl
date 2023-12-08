package com.example.tinyurl.zookeeper.connection;

import com.example.tinyurl.util.EnvContext;
import lombok.Getter;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZooKeeperConnection {
    public static final String ZooKeeperClientString = "spring.cloud.zookeeper.connect";
    // declare zookeeper instance to access ZooKeeper ensemble
    @Getter
    private ZooKeeper zk;
    final CountDownLatch connectedSignal = new CountDownLatch(1);

    // Method to connect zookeeper ensemble.
    public ZooKeeperConnection() throws IOException,InterruptedException {
        String host = EnvContext.getProperty(ZooKeeperClientString);
        zk = new ZooKeeper(host,3000, we -> {
            if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await(5, TimeUnit.SECONDS);
    }

    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zk.close();
    }

    public void createZNode(String path, byte[] data) throws InterruptedException, KeeperException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public Stat existZk(String path) throws InterruptedException, KeeperException {
        return zk.exists(path, true);
    }

    public byte[] getData(String path) throws InterruptedException, KeeperException {
        return zk.getData(path, true, null);
    }

    public void setData(String path, byte[] data) throws InterruptedException, KeeperException {
        zk.setData(path, data, zk.exists(path,true).getVersion());
    }

}
