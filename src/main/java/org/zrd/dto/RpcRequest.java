package org.zrd.dto;

import lombok.*;

/**
 * @Author zrd
 * @Date 2021/5/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameter;

    private String version;

    private String group;

    public String getRpcServiceName() {
        return this.getClassName() + this.getVersion() + this.getGroup();
    }

}
