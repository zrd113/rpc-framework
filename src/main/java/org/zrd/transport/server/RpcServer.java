package org.zrd.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcService;
import org.zrd.provider.ServiceProvider;
import org.zrd.provider.ZkServiceProviderImpl;
import org.zrd.utils.SingletonFactory;

/**
 * @Description netty服务端
 * @Author ZRD
 * @Date 2021/5/30
 */
@Slf4j
public class RpcServer {

    public static final int PORT = 8080;

    private ServiceProvider serviceProvider = SingletonFactory.getSingleton(ZkServiceProviderImpl.class);

    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            pipeline.addLast(new RpcServerHandler());
                        }
                    });

            ChannelFuture f = bootstrap.bind(PORT).sync();

            log.info("服务端开始运行，端口为【{}】", PORT);

            //AttributeKey<Map> key = AttributeKey.valueOf("registerService");
            //f.channel().attr(key).set(map);
            f.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            });
        } catch (InterruptedException e) {
            log.error("服务端运行失败", e);
        }
    }

    public void publishService(RpcService rpcService) {
        serviceProvider.publishService(rpcService);
    }
}
