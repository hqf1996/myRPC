package com.RPCService.Method;

/**
 * @Author: hqf
 * @description: HelloService��ʵ���࣬�ṩ����������Ҫ��¶�Ľӿ�
 * @Data: Create in 15:25 2019/8/11
 * @Modified By:
 */
public class HelloServiceImpl implements HelloService {

    public String hello(String name) {
        return "���" + name;
    }
}
