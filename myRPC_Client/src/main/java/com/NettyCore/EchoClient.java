package com.NettyCore;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 11:17 2019/11/7
 * @Modified By:
 */
public class EchoClient {
    private final String host;
    private final int port;
    private String name;
    private Class<?>[] parameterTypes;
    private Object[] args;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public EchoClient(String host, int port, String name, Class<?>[] parameterTypes, Object[] args) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public void run() throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(
                                    new EchoClientHandler(name, parameterTypes, args));
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).run();
    }
}
