package org.zrd;

import org.zrd.dto.RpcRequest;
import org.zrd.service.HelloService;
import org.zrd.transport.client.RpcClient;

import java.util.concurrent.ExecutionException;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
public class ClientMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RpcClient client = new RpcClient();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName("Hello");
        rpcRequest.setClassName(HelloService.class.getName());
        rpcRequest.setRequestId("1");
        final Object o = client.sendRequest(rpcRequest);
        System.out.println(o);
    }
}
