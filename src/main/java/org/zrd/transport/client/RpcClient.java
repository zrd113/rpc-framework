package org.zrd.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

/**
 * @Description netty客户端
 * @Author ZRD
 * @Date 2021/5/30
 */
@Slf4j
public class RpcClient  {
    private final Bootstrap bootstrap;
    private ChannelFuture f;

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
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new RpcClientHandler());
                    }
                });
    }



    public <T> T clientProxy(Class<T> interfaces) {
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces}, new RpcInvocationHandler());
    }

    class RpcInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            RpcRequest request = new RpcRequest();
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameter(objects);

            log.info("客户端请求参数为【{}】", request);

            CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
            AttributeKey<Object> key = AttributeKey.valueOf("response");
            f.channel().attr(key).set(completableFuture);
            f.channel().writeAndFlush(request);

            log.info("客户端请求参数发送完毕");
            log.info("等待返回结果......");

            Object data = completableFuture.get().getData();

            log.info("返回结果为{}", data);

            return data;
        }
    }
}
