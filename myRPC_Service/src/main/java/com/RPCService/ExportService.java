package com.RPCService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: hqf
 * @description: ��¶����˵���Ӧ���񣬼������Կͻ��˵�Socket����
 * @Data: Create in 15:27 2019/8/11
 * @Modified By:
 */
public class ExportService {
    /***
    * @Author: hqf
    * @Date:
    * @Description: ��һ�� V1.0
     *                ��¶�����HelloServiceImpl��ʵ�ֵķ�ʽ��ͬ��������BIO��ʽ���������߳���ͻ���������һһ��Ӧ�ġ�
    */
    public static void exportHelloService_v1(final Object service, int port) throws IOException {
        // ����Socket���������
        System.out.println("����Socket����, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            //�����Ƿ������Կͻ��˵�socket����
            final Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**��������*/
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//                        String methodName = input.readUTF();
                        String methodName = (String) input.readObject();
                        System.out.println("methodName��" + methodName);
                        Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                        System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
                        Object[] args = (Object [])input.readObject();
                        System.out.println(Arrays.toString(args));

                        /**��������������*/
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        // ������ã���������
                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                        Object result = method.invoke(service, args);
                        System.out.println("�������˴����겢������Ӧ��" + result);
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

    /**
    * @Author: hqf
    * @Date:
    * @Description: �ڶ��� V2.0
     *                ��¶�����HelloServiceImpl���������̳߳صķ�ʽ����ֹ���ж���ͻ���ͬʱ���ʷ�������ʱ���ڴ���������̲߳����õ����������
     *                ������Ȼ��BIO�ķ�ʽ�������̳߳�ֻ�ǲ�����һ��α�첽�ķ�����
    */
    public static void exportHelloService_v2(final Object service, int port) throws IOException {
        System.out.println("����Socket����, port = " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        //�����̳߳�
        ExecutorService executor = Executors.newCachedThreadPool();
        // ��������
        while (true) {
            final Socket socket = serverSocket.accept();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**��������*/
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//                        String methodName = input.readUTF();
                        String methodName = (String) input.readObject();
                        System.out.println("methodName:" + methodName);
                        Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                        System.out.println("ParameterTypes��" + Arrays.toString(parameterTypes));
                        Object[] args = (Object [])input.readObject();
                        System.out.println(Arrays.toString(args));

                        /**��������������*/
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        // ������ã���������
                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                        Object result = method.invoke(service, args);
                        System.out.println("�������˴����겢������Ӧ��" + result);
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
            });

        }
    }
}
