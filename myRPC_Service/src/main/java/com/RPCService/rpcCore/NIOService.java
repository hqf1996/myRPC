package com.RPCService.rpcCore;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 20:09 2019/8/26
 * @Modified By:
 */
public class NIOService implements Runnable {
    private Selector selector;  //多路复用器
    private ServerSocketChannel serverSocketChannel;
    private int port;
    private Object service;

    public NIOService(int port, Object service) {
        this.port = port;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);  //设置为非阻塞模式
            serverSocketChannel.socket().bind(new InetSocketAddress(port));  //绑定监听端口port
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //注册到多路复用器上，监听accept 接收就绪，准备接受新进入的连接
            Object result = null;
            while (true){
                selector.select(1000);// 阻塞在这里，可以设置休眠事件为1s，每隔一秒钟被唤醒一次，不管有没有读写事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    // 处理客户端发送过来的请求
                    if (key.isValid() && key.isAcceptable()){
                        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept(); //多路复用器监听到有新的客户端接入，处理新的请求接入，
                        // 并完成TCP三次握手，建立起物理链路。
                        sc.configureBlocking(false); //设置为非阻塞
                        sc.register(selector, SelectionKey.OP_READ); //注册到多路复用器上，监听客户端的读操作
                        System.out.println("与服务器建立链接...");
                    }
                    if(key.isValid() && key.isReadable()){
                        System.out.println("检测到客户端的读操作...");
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
                        byteBuffer.clear();
                        while (sc.read(byteBuffer) > 0){
                            System.out.println("读取客户端请求...");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
                            ObjectInputStream input = new ObjectInputStream(byteArrayInputStream);

                            String methodName = (String) input.readObject();
//                            System.out.println("methodName：" + methodName);
                            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
//                            System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
                            Object[] args = (Object [])input.readObject();
//                            System.out.println(Arrays.toString(args));

                            Method method = service.getClass().getMethod(methodName, parameterTypes);
                            result = method.invoke(service, args);
                            sc.register(selector, SelectionKey.OP_WRITE);
                        }
                    }
                    if (key.isWritable()){
                        System.out.println("检测到写操作到客户端...");
                        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
                        byteBuffer.clear();
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(result);
                        byteBuffer.put(byteArrayOutputStream.toByteArray());
                        byteBuffer.flip(); //将读转化为写模式
                        System.out.println("返回客户端调用结果: "+ result);
                        sc.write(byteBuffer);
                        // 关闭资源
                        sc.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
