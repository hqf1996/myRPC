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
 * @description: ��¶����˵���Ӧ���񣬼������Կͻ��˵�Socket����
 * @Data: Create in 15:27 2019/8/11
 * @Modified By:
 */
public class ExportService {
    /***
    * @Author: hqf
    * @Date:
    * @Description: ��һ�� V1.0
     *                ��¶�����HelloServiceImpl��ʵ�ֵķ�ʽ��ͬ��������BIO��ʽ���������߳���ͻ���������һһ��Ӧ�ġ�
    */
    public static void exportHelloService_v1(final Object service, int port) throws IOException {
        // ����Socket���������
        System.out.println("����Socket����1, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            //�����Ƿ������Կͻ��˵�socket����
            final Socket socket = serverSocket.accept();
            new Thread(new SocketThread(service, port, socket)).start();
        }
    }

    /**
    * @Author: hqf
    * @Date:
    * @Description: �ڶ��� V2.0
     *                ��¶�����HelloServiceImpl���������̳߳صķ�ʽ����ֹ���ж���ͻ���ͬʱ���ʷ�������ʱ���ڴ���������̲߳����õ����������
     *                ������Ȼ��BIO�ķ�ʽ�������̳߳�ֻ�ǲ�����һ��α�첽�ķ�����
    */
    public static void exportHelloService_v2(final Object service, int port) throws IOException {
        System.out.println("����Socket����2, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        //�����̳߳�
        ExecutorService executor = Executors.newCachedThreadPool();
        // ��������
        while (true) {
            final Socket socket = serverSocket.accept();
            executor.execute(new SocketThread(service, port, socket));
        }
    }

    /**
     * ������ V3.0
     * @param service ����NIO��ʽ����
     * @param port
     */
    public static void exportHelloService_v3(final Object service, int port){
        System.out.println("����Socket����3, port = " + port);
        NIOService nioService = new NIOService(port, service);
        new Thread(nioService).start();
    }

    /**
     *  ���İ�  V4.0  ����netty�ķ�ʽ���Ľ�
     * @param service
     * @param port
     * @throws InterruptedException
     */
    public static void exportHelloService_v4(final Object service, int port) throws InterruptedException {
        System.out.println("Netty_RPC����˹������, port = " + port);
        NettyService nettyService = new NettyService(port, service);
        nettyService.run();
    }

    /**
     *  �����   V5.0  ��ӽ�zookeeper��Ϊ��������
     * @param service
     */
    public static void exportHelloService_v5(final Object service, int port) throws InterruptedException, IOException, KeeperException {
        // �ѷ���ע�ᵽzookeeper��
        zkService zk = new zkService();
        String ConnectHostAddress = InetAddress.getLocalHost().getHostAddress() + ":2181";
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        zk.connect(ConnectHostAddress, 2000);
        zk.regist("helloService", hostAddress, String.valueOf(port));
        // ��������
        System.out.println("Netty_RPC����˹������, port = " + port);
        NettyService nettyService = new NettyService(port, service);
        nettyService.run();
    }

}
