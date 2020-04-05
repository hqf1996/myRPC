package com.NettyCore;

import com.Message.HessianSerializeDeserializeMain;
import com.Message.MSG;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 11:18 2019/11/7
 * @Modified By:
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private String name;
    private Class<?>[] parameterTypes;
    private Object[] args;

    public EchoClientHandler(String name, Class<?>[] parameterTypes, Object[] args) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    // 当客户端连接上服务端的时候触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        MSG message = new MSG();
        message.setMethodName(name);
        message.setParameterTypes(parameterTypes);
        message.setArgs(args);
        byte[] serialize = HessianSerializeDeserializeMain.serialize(message);
        System.out.println("客户端发送请求到服务端...");
//        ctx.write(message);
        ctx.write(serialize);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String msg1 = (String) msg;
        System.out.println("客户端接收服务端响应，并输出返回信息...");
        System.out.println(msg1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
