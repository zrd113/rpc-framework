package org.zrd.registry;

import org.apache.curator.framework.CuratorFramework;
import org.zrd.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zrd
 * @Date 2021/7/10
 */
public class ZkServiceRegistry implements ServiceRegistry {

    private Map<String, Object> map = new ConcurrentHashMap<>();

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress, Object object) {
        String servicePath = CuratorUtils.ZK_ROOT_PATH + "/" + rpcServiceName + "/" + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
        map.put(servicePath, object);
    }
}
