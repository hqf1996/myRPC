package com.RPCService;

import java.io.IOException;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 15:52 2019/8/11
 * @Modified By:
 */
public class provider {
    public static void main(String[] args) throws IOException {
        HelloService helloService = new HelloServiceImpl();
        ExportService.exportHelloService(helloService, 1234);
    }
}
