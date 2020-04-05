package com.RPCService.rpcCore;

import com.Message.HessianSerializeDeserializeMain;
import com.Message.MSG;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 10:58 2019/11/7
 * @Modified By:
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private Object service;

    public EchoServerHandler(Object service) {
        this.service = service;
    }

    // �����ݶ�ȡ��ʱ�����
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        Object result = dealMSG((MSG) msg);
        Object result = dealMSG(HessianSerializeDeserializeMain.deserialize((byte[]) msg));
        System.out.println("����˽�������...");
        ctx.writeAndFlush(result);
        System.out.println("����˷������ݸ��ͻ���...");
    }

    //���ζ�ȡ��ɵ���
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public Object dealMSG(MSG message) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object result = method.invoke(service, message.getArgs());
        return result;
    }
}
