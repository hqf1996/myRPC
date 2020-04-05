package com.RPCClient;

import com.ZKClient.zkClient;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;

/**
 * @Author: hqf
 * @description: ������
 * @Data: Create in 14:41 2019/8/11
 * @Modified By:
 */
public class consumer {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // ��д InvocationHandler
//        myInvocationHandler handler = new myInvocationHandler("127.0.0.1", 1234);
//        // JDK��̬��������HelloService�Ĵ�����proxy���൱���Ƕ�Ŀ�귽������ǿ�࣬�������������RPC����ͽ��ܷ������˷���ֵ
//        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
//                new Class<?>[] {HelloService.class},
//                handler);
//        proxy.hello("World");

//        nioInvocationHandler handler = new nioInvocationHandler("127.0.0.1", 1234);
//        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
//                new Class<?>[] {HelloService.class},
//                handler);
//        proxy.hello("World");

//        nettyInvocationHandler handler = new nettyInvocationHandler("127.0.0.1", 1234);
//        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
//                new Class<?>[] {HelloService.class},
//                handler);
//        proxy.hello("World");

        zkClient zk = new zkClient();
        // ����zookeeper��������ӵ���zk�������ĵ�ַ   192.168.10.105:2181   10.66.104.159:2181
        zk.connect("192.168.10.105:2181", 2000);
        // ��ý����Ϣ
        List<String> helloService = zk.getChildren("helloService");
        // ��߿������zookeeper���ؾ�����ƣ���ʱʹ������ķ�����
        Random random = new Random();
        int randomi = random.nextInt(helloService.size());
        String ipAndport = helloService.get(randomi);
        String[] split = ipAndport.split(":");
        System.out.println("��ǰ���ʵĻ����ǣ�" + split[0] + ":" + split[1]);
        nettyInvocationHandler handler = new nettyInvocationHandler(split[0], Integer.valueOf(split[1]));
        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
                new Class<?>[] {HelloService.class},
                handler);
        proxy.hello("World");
    }
}
