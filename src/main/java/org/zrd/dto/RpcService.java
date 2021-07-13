package org.zrd.dto;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/7/13
 */
public class RpcService {
    private String rpcServiceName;

    private Object service;

    public RpcService() {
    }

    public RpcService(String rpcServiceName, Object service) {
        this.rpcServiceName = rpcServiceName;
        this.service = service;
    }

    public String getRpcServiceName() {
        return rpcServiceName;
    }

    public void setRpcServiceName(String rpcServiceName) {
        this.rpcServiceName = rpcServiceName;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }
}
