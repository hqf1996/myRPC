package com.RPCClient;

import java.lang.reflect.Proxy;

/**
 * @Author: hqf
 * @description: 消费者
 * @Data: Create in 14:41 2019/8/11
 * @Modified By:
 */
public class consumer {
    public static void main(String[] args) {
        // 重写 InvocationHandler
        myInvocationHandler handler = new myInvocationHandler("127.0.0.1", 1234);
        // JDK动态代理生成HelloService的代理类proxy，相当于是对目标方法的增强类，这边用它来发送RPC请求和接受服务器端返回值
        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
                new Class<?>[] {HelloService.class},
                handler);
        proxy.hello("World");
    }
}
