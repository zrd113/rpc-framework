package org.zrd.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Description rpc服务注解
 * @Author ZRD
 * @Date 2021/8/1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface RpcService {
    //service版本
    String version() default "";

    //当一个接口有多个实现类时，用group来标识
    String group() default "";
}
