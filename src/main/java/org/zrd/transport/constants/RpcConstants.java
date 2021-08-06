package org.zrd.transport.constants;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/6
 */
public class RpcConstants {
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 12;
    public static final int HEAD_LENGTH = 12;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
