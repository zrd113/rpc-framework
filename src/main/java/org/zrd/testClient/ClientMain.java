package org.zrd.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.zrd.testServer.service.HelloController;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@ComponentScan({"org.zrd.testServer.service", "org.zrd.spring"})
public class ClientMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientMain.class);
        HelloController helloController = applicationContext.getBean(HelloController.class);
        helloController.test();
    }
}
