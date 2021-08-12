package org.zrd.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zrd.dto.RpcServiceConfig;
import org.zrd.provider.ServiceProvider;
import org.zrd.provider.ZkServiceProviderImpl;
import org.zrd.transport.codec.RpcMessageDecoder;
import org.zrd.transport.codec.RpcMessageEncoder;
import org.zrd.utils.SingletonFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Description netty服务端
 * @Author ZRD
 * @Date 2021/5/30
 */
@Slf4j
@Component
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
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new RpcMessageDecoder());
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(new RpcServerHandler());
                        }
                    });

            ChannelFuture f = bootstrap.bind(PORT).sync();

            log.info("服务端开始运行，端口为【{}】", PORT);

            f.channel().closeFuture().addListener(future -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("服务端运行失败", e);
        }
    }

    public void publishService(RpcServiceConfig rpcService) {
        serviceProvider.publishService(rpcService);
    }
}
