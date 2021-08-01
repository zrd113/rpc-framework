package org.zrd;

import org.zrd.proxy.RpcProxy;
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
        HelloService proxy = new RpcProxy(client).getProxy(HelloService.class);
        String hello = proxy.Hello();
        System.out.println(hello);
    }
}
