package org.zrd.serialize;

import org.zrd.utils.extension.SPI;

/**
 * @Description 序列化接口
 * @Author ZRD
 * @Date 2021/8/5
 */
@SPI
public interface Serializer {
    /**
     * 序列化
     * @param obj 序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * @Description: 反序列化
     * @Param bytes 序列化后的字节数组
     * @Param clazz 序列化的类型
     * @Return: 反序列化的对象
     * @Date: 2021/8/5
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
