package org.zrd.proxy;

import org.zrd.dto.RpcRequest;
import org.zrd.dto.RpcResponse;
import org.zrd.transport.client.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Description 代理类，用于发送请求
 * @Author ZRD
 * @Date 2021/7/30
 */
public class RpcProxy implements InvocationHandler {

    private final RpcClient rpcClient;

    public RpcProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setParameter(objects);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setRequestId(UUID.randomUUID().toString());

        RpcResponse<Object> response = (RpcResponse<Object>) rpcClient.sendRequest(rpcRequest);
        return  response.getData();
    }
}
