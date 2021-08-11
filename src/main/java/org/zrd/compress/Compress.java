package org.zrd.compress;

import org.zrd.utils.extension.SPI;

/**
 * @Description 压缩接口
 * @Author ZRD
 * @Date 2021/8/9
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);

}
