package org.zrd;

import org.zrd.service.HelloService;
import org.zrd.service.HelloServiceImpl;
import org.zrd.transport.server.RpcServer;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
public class ServerMain {
    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        HelloServiceImpl helloService = new HelloServiceImpl();
        server.publishService(HelloService.class.getName(), helloService);
        server.run();
    }
}
