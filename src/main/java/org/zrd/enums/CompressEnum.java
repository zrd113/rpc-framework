package org.zrd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/10
 */
@AllArgsConstructor
@Getter
public enum CompressEnum {
    GZIP((byte)1, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressEnum value : CompressEnum.values()) {
            if (value.getCode() == code) {
                return value.name;
            }
        }
        return null;
    }
}
