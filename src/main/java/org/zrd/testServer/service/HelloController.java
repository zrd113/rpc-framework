package org.zrd.testServer.service;

import org.springframework.stereotype.Component;
import org.zrd.annotation.RpcReference;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/2
 */
@Component
public class HelloController {
    @RpcReference(version = "1.0", group = "HelloServiceImpl")
    private org.zrd.testServer.service.HelloService helloService;

    @RpcReference(version = "1.0", group = "HelloServiceImpl1")
    private org.zrd.testServer.service.HelloService helloService1;

    public void test() {
        String hello = helloService.Hello();
        //String hello1 = helloService1.Hello();
        System.out.println(hello);
    }
}
