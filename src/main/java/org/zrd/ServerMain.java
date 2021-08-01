package org.zrd;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.zrd.transport.server.RpcServer;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@ComponentScan(basePackages = "org.zrd")
public class ServerMain {
    public static void main(String[] args) {
//        RpcServer server = new RpcServer();
//        Test test = new TestImpl();
//        RpcService rpcService = new RpcService();
//        rpcService.setService(test);
//        rpcService.setRpcServiceName(Test.class.getName());
//        server.publishService(rpcService);
//        server.run();

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServerMain.class);
        RpcServer server = applicationContext.getBean(RpcServer.class);
        server.run();
    }
}
