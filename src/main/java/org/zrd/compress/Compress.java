package org.zrd.compress;

/**
 * @Description 压缩接口
 * @Author ZRD
 * @Date 2021/8/9
 */
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);

}
