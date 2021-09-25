package org.zrd.testServer.service;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/7/30
 */
public class TestImpl implements Test {
    @Override
    public String test(String s) {
        return "Hello " + s;
    }
}
