package org.zrd.dto;

import lombok.*;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> {

    private T data;

    private String message;

    private String requestId;

    public static <T> RpcResponse success(T data, String requestId, String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setData(data);
        response.setMessage(message);
        response.setRequestId(requestId);
        return response;
    }

    public static <T> RpcResponse fail(String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setData(null);
        response.setMessage(message);
        return response;
    }

}
