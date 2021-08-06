package org.zrd.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcMessage;
import org.zrd.dto.RpcResponse;
import org.zrd.utils.SingletonFactory;
import org.zrd.utils.UnProcessedReqMap;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler {
    private final UnProcessedReqMap unProcessedReqMap;

    public RpcClientHandler() {
        unProcessedReqMap = SingletonFactory.getSingleton(UnProcessedReqMap.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcMessage rpcMessage = (RpcMessage) msg;

        log.info("将返回结果设置到 completableFuture 中");
        RpcResponse<Object> response = (RpcResponse<Object>) rpcMessage.getData();
        unProcessedReqMap.complete(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
