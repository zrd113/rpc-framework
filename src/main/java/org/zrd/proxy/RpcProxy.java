package org.zrd.proxy;

import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;
import org.zrd.dto.RpcServiceConfig;
import org.zrd.transport.client.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Description 代理类，用于发送请求
 * @Author ZRD
 * @Date 2021/7/30
 */
public class RpcProxy implements InvocationHandler {

    private final RpcClient rpcClient;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcProxy(RpcClient rpcClient, RpcServiceConfig rpcServiceConfig) {
        this.rpcClient = rpcClient;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .className(method.getDeclaringClass().getName())
                .parameter(objects)
                .parameterTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();

        CompletableFuture<RpcResponse<Object>> response = (CompletableFuture<RpcResponse<Object>>) rpcClient.sendRequest(rpcRequest);
        return response.get().getData();
    }
}
