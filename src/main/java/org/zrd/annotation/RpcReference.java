package org.zrd.annotation;

import java.lang.annotation.*;

/**
 * @Description rpc消费注解
 * @Author ZRD
 * @Date 2021/8/2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {
    //service版本
    String version() default "";

    //当一个接口有多个实现类时，用group来标识
    String group() default "";
}
