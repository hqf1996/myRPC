package com.RPCService.rpcCore;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 20:09 2019/8/26
 * @Modified By:
 */
public class NIOService implements Runnable {
    private Selector selector;  //��·������
    private ServerSocketChannel serverSocketChannel;
    private int port;
    private Object service;

    public NIOService(int port, Object service) {
        this.port = port;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);  //����Ϊ������ģʽ
            serverSocketChannel.socket().bind(new InetSocketAddress(port));  //�󶨼����˿�port
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //ע�ᵽ��·�������ϣ�����accept ���վ�����׼�������½��������
            Object result = null;
            while (true){
                selector.select(1000);// ����������������������¼�Ϊ1s��ÿ��һ���ӱ�����һ�Σ�������û�ж�д�¼�
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    // ����ͻ��˷��͹���������
                    if (key.isValid() && key.isAcceptable()){
                        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept(); //��·���������������µĿͻ��˽��룬�����µ�������룬
                        // �����TCP�������֣�������������·��
                        sc.configureBlocking(false); //����Ϊ������
                        sc.register(selector, SelectionKey.OP_READ); //ע�ᵽ��·�������ϣ������ͻ��˵Ķ�����
                        System.out.println("���������������...");
                    }
                    if(key.isValid() && key.isReadable()){
                        System.out.println("��⵽�ͻ��˵Ķ�����...");
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
                        byteBuffer.clear();
                        while (sc.read(byteBuffer) > 0){
                            System.out.println("��ȡ�ͻ�������...");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
                            ObjectInputStream input = new ObjectInputStream(byteArrayInputStream);

                            String methodName = (String) input.readObject();
//                            System.out.println("methodName��" + methodName);
                            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
//                            System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
                            Object[] args = (Object [])input.readObject();
//                            System.out.println(Arrays.toString(args));

                            Method method = service.getClass().getMethod(methodName, parameterTypes);
                            result = method.invoke(service, args);
                            sc.register(selector, SelectionKey.OP_WRITE);
                        }
                    }
                    if (key.isWritable()){
                        System.out.println("��⵽д�������ͻ���...");
                        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
                        byteBuffer.clear();
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(result);
                        byteBuffer.put(byteArrayOutputStream.toByteArray());
                        byteBuffer.flip(); //����ת��Ϊдģʽ
                        System.out.println("���ؿͻ��˵��ý��: "+ result);
                        sc.write(byteBuffer);
                        // �ر���Դ
                        sc.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
