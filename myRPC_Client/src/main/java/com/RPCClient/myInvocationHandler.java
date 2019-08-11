package com.RPCClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description: 重写invoke
 * @Data: Create in 14:09 2019/8/11
 * @Modified By:
 */
public class myInvocationHandler implements InvocationHandler {
    private final String host;
    private final int port;

    public myInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 与服务器端创建连接
        System.out.println("创建与服务器端的的连接...");
        Socket socket = new Socket(host, port);
        // 客户端向服务端发送请求
        System.out.println("向客户端发送请求...");
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        // 方法名
        output.writeUTF(method.getName());
        System.out.println("methodName:" + method.getName());
        // 参数类型列表
        output.writeObject(method.getParameterTypes());
        System.out.println("parameterTypes:" + Arrays.toString(method.getParameterTypes()));
        // 参数值列表
        output.writeObject(args);
        System.out.println("args:" + Arrays.toString(args));

        /**客户端读取服务端的返回*/
        System.out.println("\n客户端读取服务端的返回...");
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Object returnResult = input.readObject();
        System.out.println(returnResult);

        // 关闭资源
        socket.close();
        input.close();
        output.close();

        return returnResult;
    }
}
