package org.zrd.utils;

import lombok.extern.slf4j.Slf4j;
import org.zrd.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 用于保存尚未处理的request
 * @Author ZRD
 * @Date 2021/7/16
 */
@Slf4j
public class UnProcessedReqMap {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQ_MAP = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture completableFuture) {
        UNPROCESSED_REQ_MAP.put(requestId, completableFuture);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQ_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            log.error("未找到对应的future[{}]", rpcResponse.getRequestId());
        }
    }
}
