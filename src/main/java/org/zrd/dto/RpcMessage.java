package org.zrd.dto;

import lombok.*;

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

    private Object data;
}
