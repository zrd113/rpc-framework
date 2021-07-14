package org.zrd.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 获取单例对象的工厂类
 * @Author ZRD
 * @Date 2021/7/14
 */
public class SingletonFactory {
    private static final Map<String, Object> MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {}

    public static <T> T getSingleton(Class<T> c) {
        if (c == null) {
            throw new IllegalArgumentException();
        }

        String key = c.toString();
        if (MAP.containsKey(key)) {
            return c.cast(MAP.get(key));
        } else {
            return c.cast(MAP.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
