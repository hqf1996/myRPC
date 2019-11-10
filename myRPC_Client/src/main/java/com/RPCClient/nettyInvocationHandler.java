package com.RPCClient;

import com.NettyCore.EchoClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 16:52 2019/11/7
 * @Modified By:
 */
public class nettyInvocationHandler implements InvocationHandler {
    // 服务器端的ip地址
    private final String host;
    // 端口号
    private final int port;

    public nettyInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EchoClient echoClient = new EchoClient(host, port, method.getName(), method.getParameterTypes(), args);
        echoClient.run();
        return null;
    }
}
