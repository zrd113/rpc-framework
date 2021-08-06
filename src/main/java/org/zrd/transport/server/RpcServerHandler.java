package org.zrd.transport.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

        RpcRequest request = (RpcRequest) rpcMessageReq.getData();
        RpcResponse response = invoke(request, ctx);
        rpcMessageRes.setMessageType(RpcConstants.RESPONSE_TYPE);
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            rpcMessageRes.setData(response);
        }

        log.info("服务端调用完毕，结果为【{}】", response);

        ctx.writeAndFlush(rpcMessageRes).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        log.info("将结果返回给客户端");
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
