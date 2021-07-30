package org.zrd;

import org.zrd.dto.RpcService;
import org.zrd.service.Test;
import org.zrd.service.TestImpl;
import org.zrd.transport.server.RpcServer;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
public class ServerMain {
    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        Test test = new TestImpl();
        RpcService rpcService = new RpcService();
        rpcService.setService(test);
        rpcService.setRpcServiceName(Test.class.getName());
        server.publishService(rpcService);
        server.run();
    }
}
