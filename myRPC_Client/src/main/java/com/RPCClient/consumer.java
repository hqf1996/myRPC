package com.RPCClient;

import java.lang.reflect.Proxy;

/**
 * @Author: hqf
 * @description: ������
 * @Data: Create in 14:41 2019/8/11
 * @Modified By:
 */
public class consumer {
    public static void main(String[] args) {
        // ��д InvocationHandler
        myInvocationHandler handler = new myInvocationHandler("127.0.0.1", 1234);
        // JDK��̬��������HelloService�Ĵ�����proxy���൱���Ƕ�Ŀ�귽������ǿ�࣬�������������RPC����ͽ��ܷ������˷���ֵ
        HelloService proxy = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(),
                new Class<?>[] {HelloService.class},
                handler);
        proxy.hello("World");
    }
}
