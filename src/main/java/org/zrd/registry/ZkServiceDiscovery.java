package org.zrd.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.zrd.dto.RpcRequest;
import org.zrd.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author zrd
 * @Date 2021/7/10
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    @Override
    public InetSocketAddress findService(RpcRequest request) {
        String rpcServiceName = request.getClassName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            log.error("没有注册该服务[{}]", rpcServiceName);
        }
        String[] addrArray = serviceUrlList.get(0).split(":");
        String host = addrArray[0];
        int port = Integer.parseInt(addrArray[1]);
        return new InetSocketAddress(host, port);
    }
}
