package org.zrd.provider;

import org.zrd.dto.RpcService;

/**
 * @Description 服务提供接口
 * @Author ZRD
 * @Date 2021/7/13
 */
public interface ServiceProvider {

    /**
     * @Param rpcService 添加的服务实体
     * @Return: void
     * @Date: 2021/7/13
     */
    void addService(RpcService rpcService);

    /**
     * @Param: rpcServiceName 服务名字
     * @Return: java.lang.Object
     * @Date: 2021/7/13
     */
    Object getService(String rpcServiceName);

    /**
     * @Param: rpcService 发布的rpc服务
     * @Return: void
     * @Date: 2021/7/13
     */
    void publishService(RpcService rpcService);
}
