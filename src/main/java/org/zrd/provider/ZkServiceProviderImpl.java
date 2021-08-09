package org.zrd.provider;

import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcServiceConfig;
import org.zrd.registry.ServiceRegistry;
import org.zrd.registry.zk.ZkServiceRegistry;
import org.zrd.transport.server.RpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 服务提供实现类
 * @Author ZRD
 * @Date 2021/7/13
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {
    private Map<String, Object> serviceMap;
    private ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceRegistry = new ZkServiceRegistry();
    }

    @Override
    public void addService(RpcServiceConfig rpcService) {
        String rpcServiceName = rpcService.getRpcServiceName();
        serviceMap.put(rpcServiceName, rpcService.getService());
        log.info("添加服务：{}", rpcServiceName);
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null) {
            throw new RuntimeException("未注册该服务");
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcService) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcService);
            serviceRegistry.registerService(rpcService.getRpcServiceName(), new InetSocketAddress(host, RpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("获取本地地址发生错误");
        }
    }
}
