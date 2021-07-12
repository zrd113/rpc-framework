package org.zrd.registry;

import java.net.InetSocketAddress;

/**
 * @Author zrd
 * @Date 2021/7/10
 */
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress, Object object);
}
