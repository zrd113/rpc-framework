package org.zrd.service;

import org.springframework.stereotype.Component;
import org.zrd.annotation.RpcReference;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/2
 */
@Component
public class HelloController {
    @RpcReference
    private HelloService helloService;

    public void test() {
        String hello = helloService.Hello();
        System.out.println(hello);
    }
}
