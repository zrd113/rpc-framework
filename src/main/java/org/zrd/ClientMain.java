package org.zrd;

import org.zrd.service.HelloService;
import org.zrd.transport.client.RpcClient;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
public class ClientMain {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        HelloService clientProxy = client.clientProxy(HelloService.class);
        String s = clientProxy.Hello();
    }
}
