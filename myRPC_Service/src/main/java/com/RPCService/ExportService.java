package com.RPCService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description: 暴露服务端的相应服务，监听来自客户端的Socket请求
 * @Data: Create in 15:27 2019/8/11
 * @Modified By:
 */
public class ExportService {
    /**
    * @Author: hqf
    * @Date:
    * @Description: 暴露服务端HelloServiceImpl
    */
    public static void exportHelloService(final Object service, int port) throws IOException {
        // 建立Socket服务端请求
        System.out.println("建立Socket请求, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true){
            //监听是否有来自客户端的socket请求
            final Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**解析请求*/
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        String methodName = input.readUTF();
                        System.out.println("methodName:" + methodName);
                        Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                        System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
                        Object[] args = (Object [])input.readObject();
                        System.out.println(Arrays.toString(args));

                        /**处理请求，输出结果*/
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                        Object result = method.invoke(service, args);
                        System.out.println("服务器端处理完并返回响应：" + result);
                        output.writeObject(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
