package com.RPCClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
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
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        // 判断是否连接成功，若成功发送请求消息并读应答
        if (sc.connect(new InetSocketAddress(host, port))){
            sc.configureBlocking(false);
            System.out.println("成功建立连接...");
            sc.register(selector, SelectionKey.OP_READ);
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
        // 读取返回结果
        writeBuffer.clear();
        int count = 0;
        while ((count = sc.read(writeBuffer))!=-1){
            if (count > 0){
                System.out.println("读取到服务器端返回结果...");
                writeBuffer.flip();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(writeBuffer.array());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object result = objectInputStream.readObject();
                System.out.println(result.toString());
            }
        }
        return null;
    }
}
