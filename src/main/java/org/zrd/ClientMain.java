package org.zrd;

import org.zrd.proxy.RpcProxy;
import org.zrd.service.Test;
import org.zrd.transport.client.RpcClient;

import java.util.concurrent.ExecutionException;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
public class ClientMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RpcClient client = new RpcClient();
        Test proxy = new RpcProxy(client).getProxy(Test.class);
        String hello = proxy.test("hhhhh");
        System.out.println(hello);
    }
}
