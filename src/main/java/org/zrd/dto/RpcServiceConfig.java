package org.zrd.dto;

import lombok.*;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/7/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    private String rpcServiceName;

    private Object service;

}
