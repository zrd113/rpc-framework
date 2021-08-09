package org.zrd.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.zrd.registry.ServiceRegistry;
import org.zrd.utils.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * @Description Zookeeper服务注册实现类
 * @Author ZRD
 * @Date 2021/7/13
 */
public class ZkServiceRegistry implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
