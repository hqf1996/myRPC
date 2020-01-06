package com.ZKClient;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 15:27 2020/1/6
 * @Modified By:
 */
public class zkClient {

    ZooKeeper zkClient;

    /**
     *  初始化zookeeper集群，连接
     * @param connectString
     * @param sessionTimeout
     * @throws IOException
     */
    public void connect(String connectString, int sessionTimeout) throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    /**
     *  获取子节点并监控结点的变化
     * @param serviceName
     */
    public List<String> getChildren(String serviceName) throws KeeperException, InterruptedException {
        String path = "/" + serviceName;
//        System.out.println(path);
        List<String> children = zkClient.getChildren(path, false);
        return children;
    }
}
