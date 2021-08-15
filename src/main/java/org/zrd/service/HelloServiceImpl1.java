package org.zrd.service;

import org.zrd.annotation.RpcService;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/15
 */
@RpcService(version = "1.0", group = "HelloServiceImpl1")
public class HelloServiceImpl1 implements HelloService {
    @Override
    public String Hello() {
        return "HelloServiceImpl1";
    }
}

