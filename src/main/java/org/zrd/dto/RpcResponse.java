package org.zrd.dto;

import java.io.Serializable;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 715745410605631233L;

    private T data;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> RpcResponse success(T data, String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static <T> RpcResponse fail(String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setData(null);
        response.setMessage(message);
        return response;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
