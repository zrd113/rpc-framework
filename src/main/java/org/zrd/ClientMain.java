package org.zrd;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.zrd.service.HelloController;

import java.util.concurrent.ExecutionException;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@ComponentScan(basePackages = "org.zrd")
public class ClientMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientMain.class);
        HelloController helloController = applicationContext.getBean(HelloController.class);
        helloController.test();
    }
}
