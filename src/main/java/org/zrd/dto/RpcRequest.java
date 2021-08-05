package org.zrd.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameter;

}
