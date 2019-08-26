package com.RPCService.rpcCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
public class NIOService {
    private Selector selector;  //��·������
    private ServerSocketChannel serverSocketChannel;
    private int port;
    private Object service;

    public void exportHelloService_v3(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);  //����Ϊ������ģʽ
            serverSocketChannel.socket().bind(new InetSocketAddress(port));  //�󶨼����˿�port
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //ע�ᵽ��·�������ϣ�����accept ���վ�����׼�������½��������
            while (true){
                selector.select(1000);// �������������¼�Ϊ1s
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
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
                        while (sc.read(byteBuffer) != -1){
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
                            ObjectInputStream input = new ObjectInputStream(byteArrayInputStream);
                            String methodName = (String) input.readObject();
                            System.out.println("methodName��" + methodName);
                            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                            System.out.println("ParameterTypes:" + Arrays.toString(parameterTypes));
                            Object[] args = (Object [])input.readObject();
                            System.out.println(Arrays.toString(args));
                        }
                    }
                    it.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
