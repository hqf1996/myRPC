package com.RPCClient;

import com.ZKClient.zkClient;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;

/**
 * @Author: hqf
 * @description: 消费者
 * @Data: Create in 14:41 2019/8/11
 * @Modified By:
 */
public class consumer {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // 重写 InvocationHandler
//        myInvocationHandler handler = new myInvocationHandler("127.0.0.1", 1234);
//        // JDK动态代理生成HelloService的代理类proxy，相当于是对目标方法的增强类，这边用它来发送RPC请求和接受服务器端返回值
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
        // 连接zookeeper，这边连接的是zk服务器的地址   192.168.10.105:2181   10.66.104.159:2181
        zk.connect("192.168.10.105:2181", 2000);
        // 获得结点信息
        List<String> helloService = zk.getChildren("helloService");
        // 这边可以添加zookeeper负载均衡机制，暂时使用随机的方法吧
        Random random = new Random();
        int randomi = random.nextInt(helloService.size());
        String ipAndport = helloService.get(randomi);
        String[] split = ipAndport.split(":");
        System.out.println("当前访问的机器是：" + split[0] + ":" + split[1]);
        nettyInvocationHandler handler = new nettyInvocationHandler(split[0], Integer.valueOf(split[1]));
        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
                new Class<?>[] {HelloService.class},
                handler);
        proxy.hello("World");
    }
}
