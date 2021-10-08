package org.zrd.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcMessage;
import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;
import org.zrd.enums.CompressEnum;
import org.zrd.enums.SerializationEnum;
import org.zrd.registry.ServiceDiscovery;
import org.zrd.transport.codec.RpcMessageDecoder;
import org.zrd.transport.codec.RpcMessageEncoder;
import org.zrd.transport.constants.RpcConstants;
import org.zrd.utils.ChannelProvider;
import org.zrd.utils.SingletonFactory;
import org.zrd.utils.UnProcessedReqMap;
import org.zrd.utils.extension.ExtensionLoader;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Description netty客户端
 * @Author ZRD
 * @Date 2021/5/30
 */
@Slf4j
public class RpcClient {
    private final Bootstrap bootstrap;
    private final ChannelProvider channelProvider;
    private final ServiceDiscovery serviceDiscovery;
    private final UnProcessedReqMap unProcessedReqMap;

    public RpcClient() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcClientHandler(RpcClient.this));
                    }
                });

        channelProvider = SingletonFactory.getSingleton(ChannelProvider.class);
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        unProcessedReqMap = SingletonFactory.getSingleton(UnProcessedReqMap.class);
    }

    /**
     * @Description: 连接netty服务端并得到对应的channel
     * @Param inetSocketAddress netty服务端地址
     * @Return: io.netty.channel.Channel
     * @Date: 2021/7/16
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端[{}]连接成功", inetSocketAddress.toString());
                completableFuture.complete(future.channel());

            } else {
                log.error("客户端[{}]连接失败", inetSocketAddress.toString());
                future.channel().close();
            }
        });
        return completableFuture.get();
    }

    public Object sendRequest(RpcRequest request) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.findService(request);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unProcessedReqMap.put(request.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(request)
                    .codec(SerializationEnum.KYRO.getCode())
                    .compress(CompressEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE)
                    .status((byte) 0)
                    .attachment(new HashMap<>())
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端成功发送消息[{}]", rpcMessage);
                } else {
                    future.channel().close();
                    log.error("客户端发送失败" + future.cause());
                }
            });
        }
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
