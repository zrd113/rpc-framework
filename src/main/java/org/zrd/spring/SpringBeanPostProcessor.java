package org.zrd.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.zrd.annotation.RpcServiceAnna;
import org.zrd.dto.RpcService;
import org.zrd.provider.ServiceProvider;
import org.zrd.provider.ZkServiceProviderImpl;
import org.zrd.utils.SingletonFactory;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/1
 */
@Component
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private ServiceProvider serviceProvider;

    public SpringBeanPostProcessor() {
        serviceProvider = SingletonFactory.getSingleton(ZkServiceProviderImpl.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcServiceAnna.class)) {
            RpcService rpcService = new RpcService();
            rpcService.setService(bean);
            rpcService.setRpcServiceName(bean.getClass().getInterfaces()[0].getCanonicalName());
            serviceProvider.publishService(rpcService);
        }
        return bean;
    }
}
