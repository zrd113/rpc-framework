package org.zrd.registry;

import java.net.InetSocketAddress;

/**
 * @Description 服务注册接口
 * @Author ZRD
 * @Date 2021/7/13
 */
public interface ServiceRegistry {
    /**
     * @Description: 提供rpc服务注册
     * @Param rpcServiceName rpc服务名称
     * @Param inetSocketAddress rpc服务提供地址
     * @Return: void
     * @Date: 2021/7/13
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
