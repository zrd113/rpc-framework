package org.zrd;

import org.zrd.dto.RpcService;
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
        RpcService rpcService = new RpcService();
        server.publishService(rpcService);
        server.run();
    }
}
