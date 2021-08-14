package org.zrd.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description curator工具类
 * @Author ZRD
 * @Date 2021/7/9
 */
@Slf4j
public class CuratorUtils {

    private static CuratorFramework zkClient;

    public static final String ZK_ROOT_PATH = "/rpc";

    private static final int BASE_SLEEP_TIME = 1000;

    private static final int MAX_RETRIES = 3;

    private static final String DEFAULT_ZK_ADDRESS = "***";

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();;

    private static final Set<String> PATH_SET = ConcurrentHashMap.newKeySet();

    private CuratorUtils() {}

    public static CuratorFramework getZkClient() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        final ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(DEFAULT_ZK_ADDRESS)
                .retryPolicy(retry)
                .build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("连接ZK超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return zkClient;
    }

    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("服务已经存在，内容是：[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("服务注册成功，内容是：[{}]", path);
            }
            PATH_SET.add(path);
        } catch (Exception e) {
            log.error("服务注册失败[{}]", path);
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        PATH_SET.stream().parallel().forEach(addr -> {
            if (addr.endsWith(inetSocketAddress.toString())) {
                try {
                    zkClient.delete().forPath(addr);
                } catch (Exception e) {
                    log.error("服务删除失败[{}]", addr);
                }
            }
        });
        log.info("服务清理完毕[{}]", PATH_SET.toString());
    }
}
