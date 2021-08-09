package org.zrd.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.zrd.dto.RpcRequest;
import org.zrd.loadbalance.ConsistentHashLoadBalance;
import org.zrd.loadbalance.LoadBalance;
import org.zrd.registry.ServiceDiscovery;
import org.zrd.utils.CuratorUtils;
import org.zrd.utils.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description Zookeeper服务发现实现类
 * @Author zrd
 * @Date 2021/7/10
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = SingletonFactory.getSingleton(ConsistentHashLoadBalance.class);
    }

    @Override
    public InetSocketAddress findService(RpcRequest request) {
        String rpcServiceName = request.getClassName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            log.error("没有注册该服务[{}]", rpcServiceName);
        }
        String targetAddress = loadBalance.selectServiceAddress(serviceUrlList, request);
        String[] addrArray = targetAddress.split(":");
        String host = addrArray[0];
        int port = Integer.parseInt(addrArray[1]);
        return new InetSocketAddress(host, port);
    }
}
