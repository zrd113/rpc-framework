package org.zrd.dto;

import lombok.*;

import java.util.Map;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    private byte messageType;

    private byte codec;

    private byte compress;

    private byte status;

    private Map<String, Object> attachment;

    private Object data;
}
