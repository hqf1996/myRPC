package com.RPCService.rpcCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
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
        System.out.println("建立Socket请求, port = " + port);
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
        System.out.println("建立Socket请求, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        //创建线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        // 监听请求
        while (true) {
            final Socket socket = serverSocket.accept();
            executor.execute(new SocketThread(service, port, socket));
        }
    }

    public static void exportHelloService_v3(final Object service, int port){
        System.out.println("建立Socket请求, port = " + port);
        NIOService nioService = new NIOService(port, service);
        new Thread(nioService).start();
    }
}
