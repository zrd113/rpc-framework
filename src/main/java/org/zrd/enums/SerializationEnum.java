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
public enum SerializationEnum {
    KYRO((byte)1, "kyro"),
    PROTOSTUFF((byte)2, "protostuff");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationEnum c : SerializationEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
