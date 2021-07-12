package org.zrd.transport.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler {

    private Map<String, Object> serviceRegistry;

    public RpcServerHandler(Map<String, Object> serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Map<String, Object>> key = AttributeKey.valueOf("registerService");
        ctx.channel().attr(key).set(serviceRegistry);

        log.info("服务注册到 channel 中");
    }

    @Override
    //继承SimpleChannelInboundHandler重写channelRead0后，会自动释放msg的内存(看一下netty内存池泄露问题)
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        RpcResponse response = invoke(request, ctx);

        log.info("服务端调用完毕，结果为【{}】", response);

        ctx.writeAndFlush(response);

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

        AttributeKey<Map> key = AttributeKey.valueOf("registerService");
        Map map = ctx.channel().attr(key).get();
        Object service = map.get(className);

        log.info("服务端调用的服务为【{}】", service.getClass().getName());

        Method method = service.getClass().getMethod(methodName, argsTypes);
        Object o = method.invoke(service, args);
        RpcResponse success = RpcResponse.success(o, "执行成功");

        log.info("服务调用成功");
        return success;
    }
}
