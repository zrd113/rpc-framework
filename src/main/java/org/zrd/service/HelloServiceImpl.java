package org.zrd.service;

import org.zrd.annotation.RpcService;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@RpcService(version = "1.0", group = "HelloServiceImpl")
public class HelloServiceImpl implements HelloService {
    @Override
    public String Hello() {
        return "HelloServiceImpl";
    }
}
