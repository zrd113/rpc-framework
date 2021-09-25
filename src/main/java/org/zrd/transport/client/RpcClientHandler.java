package org.zrd.transport.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcMessage;
import org.zrd.dto.RpcResponse;
import org.zrd.enums.CompressEnum;
import org.zrd.enums.SerializationEnum;
import org.zrd.transport.constants.RpcConstants;
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
        byte messageType = rpcMessage.getMessageType();
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            log.info("客户端收到：{}", rpcMessage.getData());
        } else {
            log.info("将返回结果设置到 completableFuture 中");
            RpcResponse<Object> response = (RpcResponse<Object>) rpcMessage.getData();
            unProcessedReqMap.complete(response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("客户端写空闲...");
                RpcMessage rpcMessage = RpcMessage.builder()
                        .compress(CompressEnum.GZIP.getCode())
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .codec(SerializationEnum.KYRO.getCode())
                        .data(RpcConstants.PING)
                        .build();
                ctx.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                    //类似于CLOSE_ON_FAILURE，不能像CLOSE一样直接关闭，不然收不到响应
                    if (!future.isSuccess()) {
                        log.info("客户端发送心跳检测失败：{}", future.cause());
                        future.channel().close();
                    }
                    log.info("客户端发送心跳检测：{}", RpcConstants.PING);
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
