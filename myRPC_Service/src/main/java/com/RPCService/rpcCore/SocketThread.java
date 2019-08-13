package com.RPCService.rpcCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 20:05 2019/8/13
 * @Modified By:
 */
public class SocketThread implements Runnable {
    private Object service;
    private int port;
    private Socket socket;

    public SocketThread(Object service, int port, Socket socket) {
        this.service = service;
        this.port = port;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            /**解析请求*/
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//                        String methodName = input.readUTF();
            String methodName = (String) input.readObject();
            System.out.println("methodName：" + methodName);
            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
            System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
            Object[] args = (Object [])input.readObject();
            System.out.println(Arrays.toString(args));

            /**处理请求，输出结果*/
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            // 反射调用，处理请求
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
}
