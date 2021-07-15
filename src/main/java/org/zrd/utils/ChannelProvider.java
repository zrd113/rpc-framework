package org.zrd.utils;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 用于存储和获取Channel
 * @Author ZRD
 * @Date 2021/7/15
 */
public class ChannelProvider {
    private final Map<String, Channel> map;

    public ChannelProvider() {
        map = new ConcurrentHashMap<>();
    }

    /**
     * @Param inetSocketAddress 通过ip:port获取对应的端口
     * @Return: io.netty.channel.Channel
     * @Date: 2021/7/15
     */
    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (map.containsKey(key)) {
            Channel channel = map.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                map.remove(key);
            }
        }
        return null;
    }

    /**
     * @Param inetSocketAddress ip:port作为key
     * @Param channel channel作为value
     * @Return: void
     * @Date: 2021/7/15
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        map.put(key, channel);
    }

    /**
     * @Param inetSocketAddress 通过ip:port移出channel
     * @Return: void
     * @Date: 2021/7/15
     */
    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        map.remove(key);
    }
}
