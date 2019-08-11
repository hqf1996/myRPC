package com.RPCClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description: ��дinvoke
 * @Data: Create in 14:09 2019/8/11
 * @Modified By:
 */
public class myInvocationHandler implements InvocationHandler {
    private final String host;
    private final int port;

    public myInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // ��������˴�������
        System.out.println("������������˵ĵ�����...");
        Socket socket = new Socket(host, port);
        // �ͻ��������˷�������
        System.out.println("��ͻ��˷�������...");
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        // ������
        output.writeUTF(method.getName());
        System.out.println("methodName:" + method.getName());
        // ���������б�
        output.writeObject(method.getParameterTypes());
        System.out.println("parameterTypes:" + Arrays.toString(method.getParameterTypes()));
        // ����ֵ�б�
        output.writeObject(args);
        System.out.println("args:" + Arrays.toString(args));

        /**�ͻ��˶�ȡ����˵ķ���*/
        System.out.println("\n�ͻ��˶�ȡ����˵ķ���...");
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Object returnResult = input.readObject();
        System.out.println(returnResult);

        // �ر���Դ
        socket.close();
        input.close();
        output.close();

        return returnResult;
    }
}
