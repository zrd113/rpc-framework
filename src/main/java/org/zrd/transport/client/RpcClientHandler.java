package org.zrd.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = (RpcResponse) msg;

        log.info("将返回结果设置到 completableFuture 中");

        CompletableFuture<RpcResponse> completableFuture = null;
        AttributeKey<Object> key = AttributeKey.valueOf("response");
        completableFuture = (CompletableFuture<RpcResponse>) ctx.channel().attr(key).get();
        completableFuture.complete(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
