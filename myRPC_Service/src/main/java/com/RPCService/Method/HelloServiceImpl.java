package com.RPCService.Method;

/**
 * @Author: hqf
 * @description: HelloService的实现类，提供服务器端需要暴露的接口
 * @Data: Create in 15:25 2019/8/11
 * @Modified By:
 */
public class HelloServiceImpl implements HelloService {

    public String hello(String name) {
        return "你好" + name;
    }
}
