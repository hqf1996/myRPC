package com.RPCClient;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 8:55 2019/8/27
 * @Modified By:
 */
public class nioInvocationHandler implements InvocationHandler {
    // 服务器端的ip地址
    private final String host;
    // 端口号
    private final int port;

    public nioInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SocketChannel sc = SocketChannel.open();
        Selector selector = Selector.open();
        sc.configureBlocking(false);
        // 判断是否连接成功，若成功发送请求消息并读应答
        if (sc.connect(new InetSocketAddress(host, port))){
            System.out.println("成功建立连接...");
            sc.register(selector, SelectionKey.OP_READ);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(method.getName());
            System.out.println("methodName:" + method.getName());
            objectOutputStream.writeObject(method.getParameterTypes());
            System.out.println("parameterTypes:" + Arrays.toString(method.getParameterTypes()));
            objectOutputStream.writeObject(args);
            System.out.println("args:" + Arrays.toString(args));
            writeBuffer.put(byteArrayOutputStream.toByteArray());
            writeBuffer.flip();
            sc.write(writeBuffer);
        }else {
            System.out.println("建立连接失败...");
        }
        return null;
    }
}
