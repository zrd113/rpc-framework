package org.zrd.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.zrd.annotation.RpcReference;
import org.zrd.annotation.RpcService;
import org.zrd.dto.RpcServiceConfig;
import org.zrd.provider.ServiceProvider;
import org.zrd.provider.ZkServiceProviderImpl;
import org.zrd.proxy.RpcProxy;
import org.zrd.transport.client.RpcClient;
import org.zrd.utils.SingletonFactory;

import java.lang.reflect.Field;

/**
 * @Description 处理RpcServiceAnna和RpcReference注解
 * @Author ZRD
 * @Date 2021/8/1
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private ServiceProvider serviceProvider;
    private RpcClient rpcClient;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getSingleton(ZkServiceProviderImpl.class);
        rpcClient = new RpcClient();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .version(rpcService.version())
                    .group(rpcService.group())
                    .service(bean)
                    .rpcServiceName(bean.getClass().getInterfaces()[0].getCanonicalName() + rpcService.version() + rpcService.group())
                    .build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if (annotation != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(annotation.group())
                        .version(annotation.version())
                        .build();
                RpcProxy rpcProxy = new RpcProxy(rpcClient, rpcServiceConfig);
                Object proxy = rpcProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
