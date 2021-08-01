package org.zrd.service;

import org.springframework.stereotype.Component;
import org.zrd.annotation.RpcServiceAnna;

/**
 * @Author zrd
 * @Date 2021/5/31
 */
@RpcServiceAnna
@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String Hello() {
        return "hello zrd";
    }
}
