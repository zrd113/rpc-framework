package org.zrd.transport.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcMessage;
import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;
import org.zrd.provider.ServiceProvider;
import org.zrd.provider.ZkServiceProviderImpl;
import org.zrd.transport.constants.RpcConstants;
import org.zrd.utils.SingletonFactory;

import java.lang.reflect.Method;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler {

    private ServiceProvider serviceProvider = SingletonFactory.getSingleton(ZkServiceProviderImpl.class);

    public RpcServerHandler() {}

    @Override
    //继承SimpleChannelInboundHandler重写channelRead0后，会自动释放msg的内存(看一下netty内存池泄露问题)
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcMessage rpcMessageReq = (RpcMessage) msg;
        RpcMessage rpcMessageRes = RpcMessage.builder()
                .codec(rpcMessageReq.getCodec())
                .compress(rpcMessageReq.getCompress())
                .build();
        byte messageType = rpcMessageReq.getMessageType();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            log.info("服务端收到心跳检测：{}", RpcConstants.PING);
            rpcMessageRes.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
            rpcMessageRes.setData(RpcConstants.PONG);
        } else {
            RpcRequest request = (RpcRequest) rpcMessageReq.getData();
            RpcResponse response = invoke(request, ctx);
            rpcMessageRes.setMessageType(RpcConstants.RESPONSE_TYPE);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                rpcMessageRes.setData(response);
            }
            log.info("服务端调用完毕，结果为【{}】", response);
        }
        ctx.writeAndFlush(rpcMessageRes).addListener((ChannelFutureListener)future -> {
            //类似于CLOSE_ON_FAILURE，不能像CLOSE一样直接关闭，不然收不到响应
            if (!future.isSuccess()) {
                log.info("服务端发送结果失败：{}", future.cause());
                future.channel().close();
            }
            log.info("服务端发送结果：{}", rpcMessageRes.getData());
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("服务端读空闲，关闭链接");
                ctx.close();
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

    private RpcResponse invoke(RpcRequest request, ChannelHandlerContext ctx) throws Exception {
        Object[] args = request.getParameter();
        Class<?>[] argsTypes = request.getParameterTypes();
        String className = request.getClassName();
        String methodName = request.getMethodName();

        log.info("服务端收到的请求参数为【{}】", request);

        Object service = serviceProvider.getService(request.getClassName());

        log.info("服务端调用的服务为【{}】", service.getClass().getName());

        Method method = service.getClass().getMethod(methodName, argsTypes);
        Object o = method.invoke(service, args);
        RpcResponse success = RpcResponse.success(o, request.getRequestId(), "执行成功");

        log.info("服务调用成功");
        return success;
    }
}
