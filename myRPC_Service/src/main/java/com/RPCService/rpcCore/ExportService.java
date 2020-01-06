package com.RPCService.rpcCore;

import com.RPCService.ZKService.zkService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: hqf
 * @description: 暴露服务端的相应服务，监听来自客户端的Socket请求
 * @Data: Create in 15:27 2019/8/11
 * @Modified By:
 */
public class ExportService {
    /***
    * @Author: hqf
    * @Date:
    * @Description: 第一版 V1.0
     *                暴露服务端HelloServiceImpl，实现的方式是同步阻塞的BIO方式，服务器线程与客户端请求是一一对应的。
    */
    public static void exportHelloService_v1(final Object service, int port) throws IOException {
        // 建立Socket服务端请求
        System.out.println("建立Socket请求1, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            //监听是否有来自客户端的socket请求
            final Socket socket = serverSocket.accept();
            new Thread(new SocketThread(service, port, socket)).start();
        }
    }

    /**
    * @Author: hqf
    * @Date:
    * @Description: 第二版 V2.0
     *                暴露服务端HelloServiceImpl，采用了线程池的方式，防止当有多个客户端同时访问服务器的时候内存溢出或者线程不够用的情况发生。
     *                但是仍然是BIO的方式，引入线程池只是采用了一种伪异步的方法。
    */
    public static void exportHelloService_v2(final Object service, int port) throws IOException {
        System.out.println("建立Socket请求2, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        //创建线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        // 监听请求
        while (true) {
            final Socket socket = serverSocket.accept();
            executor.execute(new SocketThread(service, port, socket));
        }
    }

    /**
     * 第三版 V3.0
     * @param service 采用NIO方式进行
     * @param port
     */
    public static void exportHelloService_v3(final Object service, int port){
        System.out.println("建立Socket请求3, port = " + port);
        NIOService nioService = new NIOService(port, service);
        new Thread(nioService).start();
    }

    /**
     *  第四版  V4.0  采用netty的方式来改进
     * @param service
     * @param port
     * @throws InterruptedException
     */
    public static void exportHelloService_v4(final Object service, int port) throws InterruptedException {
        System.out.println("Netty_RPC服务端构建完成, port = " + port);
        NettyService nettyService = new NettyService(port, service);
        nettyService.run();
    }

    /**
     *  第五版   V5.0  添加进zookeeper作为服务治理
     * @param service
     */
    public static void exportHelloService_v5(final Object service, int port) throws InterruptedException, IOException, KeeperException {
        // 把服务注册到zookeeper上
        zkService zk = new zkService();
        String ConnectHostAddress = InetAddress.getLocalHost().getHostAddress() + ":2181";
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        zk.connect(ConnectHostAddress, 2000);
        zk.regist("helloService", hostAddress, String.valueOf(port));
        // 构建服务
        System.out.println("Netty_RPC服务端构建完成, port = " + port);
        NettyService nettyService = new NettyService(port, service);
        nettyService.run();
    }

}
