package org.zrd.loadbalance;

import org.zrd.dto.RpcRequest;

import java.util.List;

/**
 * @Description 负载均衡接口
 * @Author ZRD
 * @Date 2021/7/28
 */
public interface LoadBalance {
    /**
     * @Description: 从地址列表中选择一个地址
     * @Param serviceAddresses 地址列表
     * @Param rpcRequest
     * @Return: java.lang.String 目标地址
     * @Date: 2021/7/28
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}
