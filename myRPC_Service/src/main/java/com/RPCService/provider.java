package com.RPCService;

import com.RPCService.Method.HelloService;
import com.RPCService.Method.HelloServiceImpl;
import com.RPCService.rpcCore.ExportService;
import com.RPCService.rpcCore.NIOService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 15:52 2019/8/11
 * @Modified By:
 */
public class provider {
    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        HelloService helloService = new HelloServiceImpl();
//        ExportService.exportHelloService_v1(helloService, 1234);
//        ExportService.exportHelloService_v2(helloService, 1234);
        ExportService.exportHelloService_v3(helloService, 1234);

    }
}
