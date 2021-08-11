package org.zrd.registry;

import org.zrd.dto.RpcRequest;
import org.zrd.utils.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Description 服务发现接口
 * @Author zrd
 * @Date 2021/7/10
 */
@SPI
public interface ServiceDiscovery {
    /**
     * @Description: 通过请求参数找到对应的服务
     * @Param request
     * @Return: java.net.InetSocketAddress
     * @Date: 2021/7/10
     */
    InetSocketAddress findService(RpcRequest request);
}
