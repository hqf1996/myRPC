package com.RPCService.ZKService;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 14:56 2020/1/6
 * @Modified By:
 */
public class zkService {

    ZooKeeper zkService;

    /**
     * ��ʼ��zookeeper��Ⱥ������
     * @param connectString ���ӵ�ַ
     * @param sessionTimeout ��ʱ
     * @throws IOException
     */
    public void connect(String connectString, int sessionTimeout) throws IOException {
        zkService = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    /**
     * �ѷ���ע�ᵽzookeeper��
     * @param serviceName
     * @param ip
     * @param port
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void regist(String serviceName, String ip, String port) throws KeeperException, InterruptedException {
        String path = "/" + serviceName + "/" + ip+ ":" +port;
        String result = zkService.create(path, serviceName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        if (result != null) {
            System.out.println(serviceName + " ע�ᵽzookeeper�ɹ�: " + ip+":"+port);
        } else {
            System.out.println("ע��ʧ�ܣ�");
        }
    }
}
