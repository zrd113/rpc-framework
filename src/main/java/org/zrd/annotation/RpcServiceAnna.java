package org.zrd.annotation;

import java.lang.annotation.*;

/**
 * @Description rpc服务注解
 * @Author ZRD
 * @Date 2021/8/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcServiceAnna {
}
