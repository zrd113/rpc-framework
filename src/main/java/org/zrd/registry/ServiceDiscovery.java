package org.zrd.registry;

import org.zrd.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @Author zrd
 * @Date 2021/7/10
 */
public interface ServiceDiscovery {
    InetSocketAddress findService(RpcRequest request);
}
